package org.chiu.micro.blog.wrapper;

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
    public BlogEntity saveOrUpdate(BlogEntity blog, Optional<BlogSensitiveContentEntity> blogSensitiveContentEntity) {
        BlogEntity savedBlogEntity = blogRepository.save(blog);
        Optional<BlogSensitiveContentEntity> existedSensitiveEntity = blogSensitiveContentRepository.findByBlogId(blog.getId());
        existedSensitiveEntity.ifPresent(entity -> blogSensitiveContentRepository.deleteById(entity.getId()));
        blogSensitiveContentEntity.ifPresent(entity -> blogSensitiveContentRepository.save(entity));
        return savedBlogEntity;
    }
}
