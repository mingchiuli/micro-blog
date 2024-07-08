package org.chiu.micro.blog.wrapper;

import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.chiu.micro.blog.entity.BlogEntity;
import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.repository.BlogRepository;
import org.chiu.micro.blog.repository.BlogSensitiveContentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlogSensitiveWrapper {

    private final BlogRepository blogRepository;

    private final BlogSensitiveContentRepository blogSensitiveContentRepository;

    @Transactional
    public BlogEntity saveOrUpdate(BlogEntity blog, BlogSensitiveContentEntity blogSensitiveContentEntity) {
        BlogEntity savedBlogEntity = blogRepository.save(blog);
        Optional<BlogSensitiveContentEntity> existedSensitiveEntity = blogSensitiveContentRepository.findByBlogId(savedBlogEntity.getId());
        log.info("sss:{}", existedSensitiveEntity);
        existedSensitiveEntity.ifPresent(entity -> {
            log.info("lll:{}", entity);
            blogSensitiveContentRepository.deleteById(entity.getId());
        });
        if (Objects.nonNull(blogSensitiveContentEntity)) {
            blogSensitiveContentRepository.save(blogSensitiveContentEntity);
        }
        return savedBlogEntity;
    }
}
