package org.chiu.micro.blog.service;

import java.util.List;

import org.chiu.micro.blog.vo.BlogSensitiveContentVo;

public interface BlogSensitiveService {

    BlogSensitiveContentVo findByBlogId(Long blogId);

    List<BlogSensitiveContentVo> findByBlogId(List<Long> blogIds);
}