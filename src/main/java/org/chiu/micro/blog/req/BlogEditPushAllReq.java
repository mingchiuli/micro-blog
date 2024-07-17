package org.chiu.micro.blog.req;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogEditPushAllReq {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String content;

    @NotNull
    private Integer status;

    @NotNull
    private String link;

    @NotNull
    private Integer version;

    @NotNull
    private List<String> sensitiveContentList;
}
