package org.chiu.micro.blog.req;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DeleteBlogsReq
 */
@Data
public class DeleteBlogsReq {

    @NotEmpty
    private List<Long> ids;

    @NotEmpty
    private List<String> roles;

    @NotNull
    private Long userId;
}