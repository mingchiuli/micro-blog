package org.chiu.micro.blog.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogSensitiveContentVo {

    private Long id;

    private Long blogId;

    private String sensitiveContentList;

}
