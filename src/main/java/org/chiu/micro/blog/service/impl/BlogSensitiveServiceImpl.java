package org.chiu.micro.blog.service.impl;

import java.util.Optional;

import org.chiu.micro.blog.convertor.BlogSensitiveContentVoConvertor;
import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.repository.BlogSensitiveContentRepository;
import org.chiu.micro.blog.service.BlogSensitiveService;
import org.chiu.micro.blog.vo.BlogSensitiveContentVo;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlogSensitiveServiceImpl implements BlogSensitiveService {

    private final BlogSensitiveContentRepository blogSensitiveContentRepository;

    @Override
    public BlogSensitiveContentVo findByBlogId(Long blogId) {
        Optional<BlogSensitiveContentEntity> entityOptional = blogSensitiveContentRepository.findByBlogId(blogId);
        BlogSensitiveContentEntity entity = entityOptional.orElseGet(() -> BlogSensitiveContentEntity.builder().build());
        return BlogSensitiveContentVoConvertor.convert(entity);
    }
  
}
