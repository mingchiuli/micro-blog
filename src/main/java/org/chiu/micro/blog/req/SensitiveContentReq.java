package org.chiu.micro.blog.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensitiveContentReq {

    private Integer startIndex;

    private String content;

    private Integer type;
}
