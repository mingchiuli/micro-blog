package org.chiu.micro.blog.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SensitiveContentReq implements Serializable {

    private Integer startIndex;

    private String content;

    private Integer type;
}
