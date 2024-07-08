package org.chiu.micro.blog.wrapper;

import java.util.List;
import java.util.Objects;

import org.chiu.micro.blog.entity.BlogEntity;
import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.repository.BlogRepository;
import org.chiu.micro.blog.repository.BlogSensitiveContentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlogSensitiveWrapper {

    private final BlogRepository blogRepository;

    private final BlogSensitiveContentRepository blogSensitiveContentRepository;

    @Transactional
    public BlogEntity saveOrUpdate(BlogEntity blog, BlogSensitiveContentEntity blogSensitiveContentEntity, Long existedSensitiveId) {
        BlogEntity savedBlogEntity = blogRepository.save(blog);
        if (Objects.nonNull(existedSensitiveId)) {
            blogSensitiveContentRepository.deleteById(existedSensitiveId);
        }
        //hibernate先执行的插入
        blogSensitiveContentRepository.flush();
        if (Objects.nonNull(blogSensitiveContentEntity)) {
            blogSensitiveContentRepository.save(blogSensitiveContentEntity);
        }
        return savedBlogEntity;
    }

    @Transactional
    public void deleteByIds(List<Long> ids, List<Long> sensitiveIds) {
        blogRepository.deleteAllById(ids);
        blogSensitiveContentRepository.deleteAllById(sensitiveIds);
    }
}
