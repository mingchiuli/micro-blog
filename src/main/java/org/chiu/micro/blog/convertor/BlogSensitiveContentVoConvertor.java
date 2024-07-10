package org.chiu.micro.blog.convertor;

import java.util.*;

import org.chiu.micro.blog.entity.BlogSensitiveContentEntity;
import org.chiu.micro.blog.vo.BlogSensitiveContentVo;

public class BlogSensitiveContentVoConvertor {

    private BlogSensitiveContentVoConvertor() {}

    public static BlogSensitiveContentVo convert(List<BlogSensitiveContentEntity> entities) {
        if (!entities.isEmpty()) {
            return BlogSensitiveContentVo.builder()
                    .blogId(entities.getFirst().getBlogId())
                    .sensitiveContent(entities.stream()
                            .map(BlogSensitiveContentEntity::getSensitiveContent)
                            .toList())
                    .build();
        }
        return BlogSensitiveContentVo.builder().build();
    }

    public static List<BlogSensitiveContentVo> convertBatch(List<BlogSensitiveContentEntity> entities) {
        var map = new HashMap<Long, List<String>>();

        entities.forEach(item -> {
            Long blogId = item.getBlogId();
            map.putIfAbsent(blogId, new ArrayList<>());
            map.computeIfPresent(blogId, (k, v) -> {
                v.add(item.getSensitiveContent());
                return v;
            });
        });

        List<BlogSensitiveContentVo> res = new ArrayList<>();
        map.forEach((k, v) -> res.add(BlogSensitiveContentVo.builder()
                .blogId(k)
                .sensitiveContent(v)
                .build()));
        return res;
    }
}
