package org.chiu.micro.blog.convertor;

import org.chiu.micro.blog.entity.BlogEntity;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;

public class BlogEntityRpcVoConvertor {

    private BlogEntityRpcVoConvertor(){}

    public static BlogEntityRpcVo convert(BlogEntity blogEntity) {

        return BlogEntityRpcVo.builder()
                .id(blogEntity.getId())
                .link(blogEntity.getLink())
                .readCount(blogEntity.getReadCount())
                .description(blogEntity.getDescription())
                .content(blogEntity.getContent())
                .userId(blogEntity.getUserId())
                .created(blogEntity.getCreated())
                .updated(blogEntity.getUpdated())
                .status(blogEntity.getStatus())
                .build();
  }


}
