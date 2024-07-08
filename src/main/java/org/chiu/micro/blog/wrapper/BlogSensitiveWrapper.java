package org.chiu.micro.blog.wrapper;

import java.util.List;
import java.util.Optional;

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
        Optional.ofNullable(existedSensitiveId).ifPresent(id -> blogSensitiveContentRepository.deleteById(id));
        //hibernate先执行的插入
        blogSensitiveContentRepository.flush();
        Optional.ofNullable(blogSensitiveContentEntity).ifPresent(entity -> blogSensitiveContentRepository.save(entity));
        return savedBlogEntity;
    }

    @Transactional
    public void deleteByIds(List<Long> ids, List<Long> sensitiveIds) {
        blogRepository.deleteAllById(ids);
        blogSensitiveContentRepository.deleteAllById(sensitiveIds);
    }
}
