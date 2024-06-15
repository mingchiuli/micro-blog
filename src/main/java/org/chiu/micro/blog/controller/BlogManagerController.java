package org.chiu.micro.blog.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.chiu.micro.blog.service.BlogManagerService;
import org.chiu.micro.blog.vo.BlogDeleteVo;
import org.chiu.micro.blog.vo.BlogEntityVo;
import org.chiu.micro.blog.req.BlogEntityReq;
import org.chiu.micro.blog.req.DeleteBlogsReq;
import org.chiu.micro.blog.lang.Result;
import org.chiu.micro.blog.page.PageAdapter;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-01 9:28 pm
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/blog")
@Validated
public class BlogManagerController {

    private final BlogManagerService blogManagerService;

    @PostMapping("/save/{userId}")
    public Result<Void> saveOrUpdate(@RequestBody @Valid BlogEntityReq blog, @PathVariable Long userId) {
        return Result.success(() -> blogManagerService.saveOrUpdate(blog, userId));
    }

    @PostMapping("/delete")
    public Result<Void> deleteBlogs(@RequestBody @Valid DeleteBlogsReq req) {
        return Result.success(() -> blogManagerService.deleteBatch(req.getIds(), req.getUserId(), req.getRoles()));
    }

    @GetMapping("/lock/{blogId}/{userId}")
    public Result<String> setBlogToken(@PathVariable(value = "blogId") Long blogId,
                                       @PathVariable(value = "userId") Long userId) {
        return Result.success(() -> blogManagerService.setBlogToken(blogId, userId));
    }

    @GetMapping("/blogs/{userId}")
    public Result<PageAdapter<BlogEntityVo>> getAllBlogs(@RequestParam(defaultValue = "1") Integer currentPage,
                                                         @RequestParam(defaultValue = "5") Integer size,
                                                         @RequestBody @NotEmpty List<String> roles,
                                                         @PathVariable Long userId) {
        return Result.success(() -> blogManagerService.findAllABlogs(currentPage, size, userId, roles));
    }

    @GetMapping("/deleted/{userId}")
    public Result<PageAdapter<BlogDeleteVo>> getDeletedBlogs(@RequestParam Integer currentPage,
                                                             @RequestParam Integer size,
                                                             @PathVariable Long userId) {
        return Result.success(() -> blogManagerService.findDeletedBlogs(currentPage, size, userId));
    }

    @GetMapping("/recover/{idx}/{userId}")
    public Result<Void> recoverDeletedBlog(@PathVariable(value = "idx") Integer idx, 
                                           @PathVariable(value = "userId") Long userId) {
        return Result.success(() -> blogManagerService.recoverDeletedBlog(idx, userId));
    }

    @PostMapping("/oss/upload/{userId}")
    public Result<String> uploadOss(@RequestParam MultipartFile image, 
                                    @PathVariable Long userId) {
        return Result.success(() -> blogManagerService.uploadOss(image, userId));
    }

    @GetMapping("/oss/delete")
    public Result<Void> deleteOss(@RequestParam String url) {
        return Result.success(() -> blogManagerService.deleteOss(url));
    }

    @GetMapping("/download")
    public Result<Void> download(HttpServletResponse response) {
        blogManagerService.download(response);
        return Result.success();
    }

}
