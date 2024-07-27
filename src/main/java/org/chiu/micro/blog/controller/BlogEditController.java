package org.chiu.micro.blog.controller;

import org.chiu.micro.blog.vo.BlogEditVo;
import org.chiu.micro.blog.lang.Result;
import org.chiu.micro.blog.req.BlogEditPushAllReq;
import org.chiu.micro.blog.service.BlogEditService;
import org.chiu.micro.blog.valid.PushAllValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/blog")
@Validated
public class BlogEditController {

    private final BlogEditService blogEditService;
  
    @PostMapping("/edit/push/all/{userId}")
    public Result<Void> pullSaveBlog(@RequestBody @PushAllValue BlogEditPushAllReq blog,
                                     @PathVariable Long userId) {
        return Result.success(() -> blogEditService.pushAll(blog, userId));
    }

    @GetMapping("/edit/pull/echo/{userId}")
    public Result<BlogEditVo> getEchoDetail(@RequestParam(value = "blogId", required = false) Long id,
                                            @PathVariable Long userId) {
        return Result.success(() -> blogEditService.findEdit(id, userId));
    }
}
