package org.chiu.micro.blog.provider;

import java.util.List;

import org.chiu.micro.blog.lang.Result;
import org.chiu.micro.blog.service.BlogSensitiveService;
import org.chiu.micro.blog.vo.BlogSensitiveContentVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/inner")
@RequiredArgsConstructor
@Validated
public class BlogSensitiveProvider {

    private final BlogSensitiveService blogSensitiveService;

    @GetMapping("/blog/sensitive/{blogId}")
    public Result<BlogSensitiveContentVo> findById(@PathVariable Long blogId) {
        return Result.success(() -> blogSensitiveService.findByBlogId(blogId));
    }

    @PostMapping("/blog/sensitive")
    public Result<List<BlogSensitiveContentVo>> findById(@RequestBody @NotEmpty List<Long> blogId) {
        return Result.success(() -> blogSensitiveService.findByBlogId(blogId));
    }
    

}
