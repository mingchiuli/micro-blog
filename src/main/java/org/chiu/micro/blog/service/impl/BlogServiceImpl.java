package org.chiu.micro.blog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.chiu.micro.blog.exception.MissException;
import org.chiu.micro.blog.page.PageAdapter;
import org.chiu.micro.blog.constant.BlogOperateEnum;
import org.chiu.micro.blog.constant.BlogOperateMessage;
import org.chiu.micro.blog.repository.BlogSensitiveContentRepository;
import org.chiu.micro.blog.utils.JsonUtils;
import org.chiu.micro.blog.utils.OssSignUtils;
import org.chiu.micro.blog.convertor.BlogDeleteVoConvertor;
import org.chiu.micro.blog.convertor.BlogEntityRpcVoConvertor;
import org.chiu.micro.blog.convertor.BlogEntityVoConvertor;
import org.chiu.micro.blog.entity.BlogEntity;
import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.dto.UserEntityDto;
import org.chiu.micro.blog.event.BlogOperateEvent;
import org.chiu.micro.blog.repository.BlogRepository;
import org.chiu.micro.blog.req.BlogEntityReq;
import org.chiu.micro.blog.req.ImgUploadReq;
import org.chiu.micro.blog.rpc.OssHttpService;
import org.chiu.micro.blog.rpc.wrapper.UserHttpServiceWrapper;
import org.chiu.micro.blog.service.BlogService;
import org.chiu.micro.blog.vo.BlogDeleteVo;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.chiu.micro.blog.vo.BlogEntityVo;
import org.chiu.micro.blog.wrapper.BlogSensitiveWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.chiu.micro.blog.lang.Const.*;
import static org.chiu.micro.blog.lang.StatusEnum.*;
import static org.chiu.micro.blog.lang.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {

    private final JsonUtils jsonUtils;

    private final UserHttpServiceWrapper userHttpServiceWrapper;

    private final OssHttpService ossHttpService;

    private final OssSignUtils ossSignUtils;

    private final ApplicationContext applicationContext;

    private final BlogRepository blogRepository;

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final ResourceLoader resourceLoader;

    private final BlogSensitiveWrapper blogSensitiveWrapper;

    private final BlogSensitiveContentRepository blogSensitiveContentRepository;

    @Value("${blog.highest-role}")
    private String highestRole;

    @Value("${blog.read.page-prefix}")
    private String readPrefix;

    @Qualifier("commonExecutor")
    private final ExecutorService taskExecutor;

    @Value("${blog.oss.base-url}")
    private String baseUrl;

    private String hotBlogsScript;

    private String blogDeleteScript;

    private String listDeleteScript;

    private String recoverDeleteScript;

    @PostConstruct
    @SneakyThrows
    private void init() {
        Resource hotBlogsResource = resourceLoader
                .getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/hot-blogs.lua");
        hotBlogsScript = hotBlogsResource.getContentAsString(StandardCharsets.UTF_8);
        Resource blogDeleteResource = resourceLoader
                .getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/blog-delete.lua");
        blogDeleteScript = blogDeleteResource.getContentAsString(StandardCharsets.UTF_8);
        Resource listDeleteResource = resourceLoader
                .getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/list-delete.lua");
        listDeleteScript = listDeleteResource.getContentAsString(StandardCharsets.UTF_8);
        Resource recoverDeleteResource = resourceLoader
                .getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/recover-delete.lua");
        recoverDeleteScript = recoverDeleteResource.getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    @Override
    public void download(HttpServletResponse response) {
        ServletOutputStream outputStream = response.getOutputStream();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Set<BlogEntity> items = Collections.newSetFromMap(new ConcurrentHashMap<>());
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        long total = blogRepository.count();
        int pageSize = 20;
        int totalPage = (int) (total % pageSize == 0 ? total / pageSize : total / pageSize + 1);

        for (int i = 1; i <= totalPage; i++) {
            PageRequest pageRequest = PageRequest.of(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                Page<BlogEntity> page = blogRepository.findAll(pageRequest);
                items.addAll(page.getContent());
            }, taskExecutor);
            completableFutures.add(completableFuture);
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).get(1000, TimeUnit.MILLISECONDS);
        BlogEntity[] blogs = items.toArray(new BlogEntity[0]);
        int len = blogs.length;

        for (int i = 0; i < len; i++) {
            if (i == 0) {
                // [
                outputStream.write(new byte[] { 91 });
            }

            byte[] bytes = objectMapper.writeValueAsBytes(blogs[i]);
            outputStream.write(bytes);
            if (i != len - 1) {
                // ,
                outputStream.write(new byte[] { 44 });
            }

            if (i == len - 1) {
                // ]
                outputStream.write(new byte[] { 93 });
            }
        }
        outputStream.flush();
        outputStream.close();
    }

    @SneakyThrows
    @Override
    public String uploadOss(ImgUploadReq image, Long userId) {
        Assert.notNull(image.getData(), UPLOAD_MISS.getMsg());
        String uuid = UUID.randomUUID().toString();
        String originalFilename = image.getFileName();
        originalFilename = Optional.ofNullable(originalFilename)
                .orElseGet(() -> UUID.randomUUID().toString())
                .replace(" ", "");
        UserEntityDto user = userHttpServiceWrapper.findById(userId);
        String objectName = user.getNickname() + "/" + uuid + "-" + originalFilename;
        byte[] imageBytes = image.getData();

        Map<String, String> headers = new HashMap<>();
        String gmtDate = ossSignUtils.getGMTDate();
        headers.put(HttpHeaders.DATE, gmtDate);
        headers.put(HttpHeaders.AUTHORIZATION, ossSignUtils.getAuthorization(objectName, HttpMethod.PUT.name(), "image/jpg"));
        headers.put(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.put(HttpHeaders.CONTENT_TYPE, "image/jpg");
        ossHttpService.putOssObject(objectName, imageBytes, headers);
        // https://bloglmc.oss-cn-hangzhou.aliyuncs.com/admin/42166d224f4a20a45eca28b691529822730ed0ee.jpeg
        return baseUrl + "/" + objectName;
    }

    @Override
    public void deleteOss(String url) {
        String objectName = url.replace(baseUrl + "/", "");
        Map<String, String> headers = new HashMap<>();
        String gmtDate = ossSignUtils.getGMTDate();
        headers.put(HttpHeaders.DATE, gmtDate);
        headers.put(HttpHeaders.AUTHORIZATION, ossSignUtils.getAuthorization(objectName, HttpMethod.DELETE.name(), ""));
        ossHttpService.deleteOssObject(objectName, headers);
    }

    @Override
    public String setBlogToken(Long blogId, Long userId) {
        Long dbUserId = blogRepository.findUserIdById(blogId);

        if (Objects.equals(userId, dbUserId)) {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(READ_TOKEN.getInfo() + blogId, token, 24, TimeUnit.HOURS);
            return readPrefix + blogId + "?token=" + token;
        }
        throw new MissException(USER_MISS.getMsg());
    }

    @Override
    public void saveOrUpdate(BlogEntityReq blog, Long userId) {
        Long blogId = blog.getId();
        BlogEntity blogEntity;

        if (Objects.nonNull(blogId)) {
            blogEntity = blogRepository.findById(blogId)
                    .orElseThrow(() -> new MissException(NO_FOUND.getMsg()));
            Assert.isTrue(Objects.equals(blogEntity.getUserId(), userId), EDIT_NO_AUTH.getMsg());
        } else {
            blogEntity = BlogEntity.builder()
                    .userId(userId)
                    .readCount(0L)
                    .build();
        }

        BeanUtils.copyProperties(blog, blogEntity);

        List<BlogSensitiveContentEntity> blogSensitiveContentEntityList = blog.getSensitiveContentList().stream()
                .distinct()
                .map(item -> BlogSensitiveContentEntity.builder()
                        .blogId(blog.getId())
                        .endIndex(item.getEndIndex())
                        .startIndex(item.getStartIndex())
                        .type(item.getType())
                        .build())
                .toList();

        List<Long> existedSensitiveIds = blogSensitiveContentRepository.findByBlogId(blogId)
                .stream()
                .map(BlogSensitiveContentEntity::getId)
                .toList();

        BlogEntity saved = blogSensitiveWrapper.saveOrUpdate(blogEntity, blogSensitiveContentEntityList, existedSensitiveIds);

        // 通知消息给mq,更新并删除缓存
        // 防止重复消费
        BlogOperateEnum type;
        if (Objects.nonNull(blogId)) {
            type = BlogOperateEnum.UPDATE;
        } else {
            type = BlogOperateEnum.CREATE;
            blogId = saved.getId();
        }

        var blogSearchIndexMessage = new BlogOperateMessage(blogId, type, blogEntity.getCreated().getYear());
        applicationContext.publishEvent(new BlogOperateEvent(this, blogSearchIndexMessage));
    }

    @Override
    @SuppressWarnings("all")
    public PageAdapter<BlogEntityVo> findAllABlogs(Integer currentPage, Integer size, Long userId, List<String> roles) {

        var pageRequest = PageRequest.of(currentPage - 1, size, Sort.by("created").descending());
        Page<BlogEntity> page = roles.contains(highestRole) ?
                blogRepository.findAll(pageRequest) :
                blogRepository.findAllByUserId(pageRequest, userId);

        List<BlogEntity> items = page.getContent();
        List<String> ids = items.stream()
                .map(item -> String.valueOf(item.getId()))
                .toList();

        List<String> res = redisTemplate.execute(RedisScript.of(hotBlogsScript, List.class),
                Collections.singletonList(HOT_READ.getInfo()),
                jsonUtils.writeValueAsString(ids));

        Map<Long, Integer> readMap = new HashMap<>();
        for (int i = 0; i < res.size(); i += 2) {
            readMap.put(Long.valueOf(res.get(i)), Integer.valueOf(res.get(i + 1)));
        }

        return BlogEntityVoConvertor.convert(page, readMap, userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageAdapter<BlogDeleteVo> findDeletedBlogs(Integer currentPage, Integer size, Long userId) {

        List<BlogEntity> deletedBlogs = Optional
                .ofNullable(redisTemplate.opsForList().range(QUERY_DELETED.getInfo() + userId, 0, -1))
                .orElseGet(ArrayList::new).stream()
                .map(blogStr -> jsonUtils.readValue(blogStr, BlogEntity.class))
                .toList();

        int l = 0;
        for (BlogEntity blog : deletedBlogs) {
            if (LocalDateTime.now().minusDays(7).isAfter(blog.getUpdated())) {
                l++;
            } else {
                break;
            }
        }

        int start = (currentPage - 1) * size;

        List<String> resp = Optional.ofNullable(
                redisTemplate.execute(RedisScript.of(listDeleteScript, List.class),
                        Collections.singletonList(QUERY_DELETED.getInfo() + userId),
                        String.valueOf(l), "-1", String.valueOf(size - 1), String.valueOf(start)))
                .orElseGet(ArrayList::new);

        List<String> respList = resp.subList(0, resp.size() - 1);
        Long total = Long.valueOf(resp.getLast());

        List<BlogEntity> list = respList.stream()
                .map(str -> jsonUtils.readValue(str, BlogEntity.class))
                .toList();

        return BlogDeleteVoConvertor.convert(l, list, currentPage, size, total);
    }

    @Override
    public void recoverDeletedBlog(Integer idx, Long userId) {

        String str = Optional.ofNullable(
                redisTemplate.execute(RedisScript.of(recoverDeleteScript, String.class),
                        Collections.singletonList(QUERY_DELETED.getInfo() + userId),
                        String.valueOf(idx)))
                .orElse("");

        if (!StringUtils.hasLength(str)) {
            return;
        }

        BlogEntity tempBlog = jsonUtils.readValue(str, BlogEntity.class);
        tempBlog.setStatus(HIDE.getCode());
        BlogEntity blog = blogRepository.save(tempBlog);

        var blogSearchIndexMessage = new BlogOperateMessage(blog.getId(), BlogOperateEnum.CREATE, blog.getCreated().getYear());
        applicationContext.publishEvent(new BlogOperateEvent(this, blogSearchIndexMessage));
    }

    @Override
    public void deleteBatch(List<Long> ids, Long userId, List<String> roles) {
        List<BlogEntity> blogList = new ArrayList<>();
        ids.forEach(id -> {
            BlogEntity blogEntity = blogRepository.findById(id)
                    .orElseThrow(() -> new MissException(NO_FOUND.getMsg()));
            if (!Objects.equals(blogEntity.getUserId(), userId) && !roles.contains(highestRole)) {
                throw new MissException(DELETE_NO_AUTH.getMsg());
            }
            blogList.add(blogEntity);
        });

        List<BlogSensitiveContentEntity> blogSensitiveContent = blogSensitiveContentRepository.findByBlogIdIn(ids);
        List<Long> sensitiveIds = blogSensitiveContent.stream().map(BlogSensitiveContentEntity::getId).toList();
        blogSensitiveWrapper.deleteByIds(ids, sensitiveIds);

        blogList.forEach(blogEntity -> {
            blogEntity.setUpdated(LocalDateTime.now());
            redisTemplate.execute(RedisScript.of(blogDeleteScript),
                    Collections.singletonList(QUERY_DELETED.getInfo() + userId),
                    jsonUtils.writeValueAsString(blogEntity), A_WEEK.getInfo());

            var blogSearchIndexMessage = new BlogOperateMessage(blogEntity.getId(), BlogOperateEnum.REMOVE, blogEntity.getCreated().getYear());
            applicationContext.publishEvent(new BlogOperateEvent(this, blogSearchIndexMessage));
        });

    }

    @Override
    public BlogEntityRpcVo findById(Long blogId) {
        BlogEntity blogEntity = blogRepository.findById(blogId)
                .orElseThrow(() -> new MissException(NO_FOUND.getMsg()));
        return BlogEntityRpcVoConvertor.convert(blogEntity);
    }

    @Override
    public BlogEntityRpcVo findByIdAndUserId(Long blogId, Long userId) {
        BlogEntity blogEntity = blogRepository.findById(blogId)
                .orElseThrow(() -> new MissException(NO_FOUND.getMsg()));
        if (!Objects.equals(userId, blogEntity.getUserId())) {
            throw new MissException(NO_FOUND.getMsg());
        }
        return BlogEntityRpcVoConvertor.convert(blogEntity);
    }

    @Override
    public List<BlogEntityRpcVo> findAllById(List<Long> ids) {
        List<BlogEntity> blogEntities = blogRepository.findAllById(ids);
        return BlogEntityRpcVoConvertor.convert(blogEntities);
    }

    @Override
    public List<Integer> getYears() {
        return blogRepository.getYears();
    }

    @Override
    public Long count() {
        return blogRepository.count();
    }

    @Override
    public List<Long> findIds(Integer pageNo, Integer pageSize) {
        var pageRequest = PageRequest.of(pageNo - 1,
                pageSize);
        return blogRepository.findIds(pageRequest);
    }

    @Override
    public void setReadCount(Long blogId) {
        blogRepository.setReadCount(blogId);
    }

    @Override
    public Integer findStatusById(Long blogId) {
        return blogRepository.findStatusById(blogId);
    }

    @Override
    public PageAdapter<BlogEntityRpcVo> findPage(Integer pageNo, Integer pageSize) {
        var pageRequest = PageRequest.of(pageNo - 1,
                pageSize,
                Sort.by("created").descending());

        Page<BlogEntity> page = blogRepository.findAll(pageRequest);
        return BlogEntityRpcVoConvertor.convert(page);
    }

    @Override
    public PageAdapter<BlogEntityRpcVo> findPageByCreatedBetween(Integer pageNo, Integer pageSize, LocalDateTime start, LocalDateTime end) {
        var pageRequest = PageRequest.of(pageNo - 1,
                pageSize,
                Sort.by("created").descending());
        Page<BlogEntity> page = blogRepository.findAllByCreatedBetween(pageRequest, start, end);
        return BlogEntityRpcVoConvertor.convert(page);
    }

    @Override
    public Long countByCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return blogRepository.countByCreatedBetween(start, end);
    }

    @Override
    public Long getPageCountYear(LocalDateTime created, LocalDateTime start, LocalDateTime end) {
        return blogRepository.getPageCountYear(created, start, end);
    }

    @Override
    public Long countByCreatedGreaterThanEqual(LocalDateTime created) {
        return blogRepository.countByCreatedGreaterThanEqual(created);
    }
}
