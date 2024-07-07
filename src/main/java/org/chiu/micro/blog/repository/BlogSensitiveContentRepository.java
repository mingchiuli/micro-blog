package org.chiu.micro.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;

public interface BlogSensitiveContentRepository extends JpaRepository<BlogSensitiveContentEntity, Long> {

    Optional<BlogSensitiveContentEntity> findByBlogId(Long blogId);
  
}