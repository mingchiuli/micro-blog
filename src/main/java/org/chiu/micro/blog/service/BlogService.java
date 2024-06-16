package org.chiu.micro.blog.service;

import jakarta.servlet.http.HttpServletResponse;
import org.chiu.micro.blog.page.PageAdapter;
import org.chiu.micro.blog.req.BlogEntityReq;
import org.chiu.micro.blog.vo.BlogDeleteVo;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.chiu.micro.blog.vo.BlogEntityVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface BlogService {

    void saveOrUpdate(BlogEntityReq blog, Long userId);

    PageAdapter<BlogEntityVo> findAllABlogs(Integer currentPage, Integer size, Long userId, List<String> roles);

    void recoverDeletedBlog(Integer idx, Long userId);

    PageAdapter<BlogDeleteVo> findDeletedBlogs(Integer currentPage, Integer size, Long userId);


    void deleteBatch(List<Long> ids, Long userId, List<String> roles);

    String uploadOss(MultipartFile image, Long userId);

    void deleteOss(String url);

    String setBlogToken(Long blogId, Long userId);

    void download(HttpServletResponse response);

    BlogEntityRpcVo findById(Long blogId);

    BlogEntityRpcVo findByIdAndUserId(Long blogId, Long userId);

    List<BlogEntityRpcVo> findAllById(List<Long> ids);

    List<Integer> getYears();

    Long count();

    List<Long> findIds(Pageable pageRequest);

    void setReadCount(Long blogId);

    Integer findStatusById(Long blogId);

    Page<BlogEntityRpcVo> findPage(PageRequest pageRequest);

    Page<BlogEntityRpcVo> findPageByCreatedBetween(PageRequest pageRequest, LocalDateTime start, LocalDateTime end);

    Long countByCreatedBetween(LocalDateTime start, LocalDateTime end);

    Long getPageCountYear(LocalDateTime created, LocalDateTime start, LocalDateTime end);

    Long countByCreatedGreaterThanEqual(LocalDateTime created);
}