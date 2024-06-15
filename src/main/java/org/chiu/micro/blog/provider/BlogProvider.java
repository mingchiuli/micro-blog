package org.chiu.micro.blog.provider;

import org.chiu.micro.blog.service.BlogService;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * BlogProvider
 */
@RestController
@RequestMapping(value = "/inner")
@RequiredArgsConstructor
@Validated
public class BlogProvider {

    private final BlogService blogService;

    @GetMapping("/blog/{blogId}")
    public BlogEntityRpcVo findById(@PathVariable Long blogId) {
        return blogService.findById(blogId);
    }

    @GetMapping("/blog/{blogId}/{userId}")
    public BlogEntityRpcVo findByIdAndUserId(@PathVariable(value = "blogId") Long blogId,
                                             @PathVariable(value = "userId") Long userId) {
        return blogService.findByIdAndUserId(blogId, userId);
    }

}