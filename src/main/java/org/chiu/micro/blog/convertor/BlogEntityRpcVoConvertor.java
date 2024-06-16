package org.chiu.micro.blog.convertor;

import java.util.ArrayList;
import java.util.List;

import org.chiu.micro.blog.entity.BlogEntity;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    public static List<BlogEntityRpcVo> convert(List<BlogEntity> blogEntities) {

        List<BlogEntityRpcVo> out = new ArrayList<>();
        blogEntities.forEach(item -> out.add(BlogEntityRpcVo.builder()
                .id(item.getId())
                .link(item.getLink())
                .readCount(item.getReadCount())
                .description(item.getDescription())
                .content(item.getContent())
                .userId(item.getUserId())
                .created(item.getCreated())
                .updated(item.getUpdated())
                .status(item.getStatus())
                .build()));
        return out;
    }

    public static Page<BlogEntityRpcVo> convert(Page<BlogEntity> page) {
        List<BlogEntityRpcVo> out = new ArrayList<>();
        page.getContent().forEach(item -> out.add(BlogEntityRpcVo.builder()
                .id(item.getId())
                .link(item.getLink())
                .readCount(item.getReadCount())
                .description(item.getDescription())
                .content(item.getContent())
                .userId(item.getUserId())
                .created(item.getCreated())
                .updated(item.getUpdated())
                .status(item.getStatus())
                .build()));
        Pageable pageable = page.getPageable();
        return new PageImpl<>(out, pageable, page.getTotalElements());
    }
}
