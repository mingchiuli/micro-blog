package org.chiu.micro.blog.convertor;

import java.util.ArrayList;
import java.util.List;

import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.vo.BlogSensitiveContentVo;

public class BlogSensitiveContentVoConvertor {

    private BlogSensitiveContentVoConvertor() {}

    public static BlogSensitiveContentVo convert(BlogSensitiveContentEntity entitiy) {
        return BlogSensitiveContentVo.builder()
                .blogId(entitiy.getBlogId())
                .id(entitiy.getId())
                .sensitiveContentList(entitiy.getSensitiveContentList())
                .build();
    }

    public static List<BlogSensitiveContentVo> convert(List<BlogSensitiveContentEntity> entities) {
        List<BlogSensitiveContentVo> res = new ArrayList<>();
        entities.stream().forEach(item ->
                res.add(BlogSensitiveContentVo.builder()
                        .blogId(item.getBlogId())
                        .id(item.getId())
                        .sensitiveContentList(item.getSensitiveContentList())
                        .build()));
        return res;
    }
}
