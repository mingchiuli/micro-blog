package org.chiu.micro.blog.vo;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SensitiveContentVo {

    private Integer startIndex;

    private String content;

    private Integer type;
}
