package org.chiu.micro.blog.provider;

import java.time.LocalDateTime;
import java.util.List;

import org.chiu.micro.blog.lang.Result;
import org.chiu.micro.blog.service.BlogService;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

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
    public Result<BlogEntityRpcVo> findById(@PathVariable Long blogId) {
        return Result.success(() -> blogService.findById(blogId));
    }

    @GetMapping("/blog/{blogId}/{userId}")
    public Result<BlogEntityRpcVo> findByIdAndUserId(@PathVariable(value = "blogId") Long blogId,
                                                     @PathVariable(value = "userId") Long userId) {
        return Result.success(() -> blogService.findByIdAndUserId(blogId, userId));
    }

    @PostMapping("/blog/batch")
    public Result<List<BlogEntityRpcVo>> findAllById(@RequestBody List<Long> ids) {
        return Result.success(() -> blogService.findAllById(ids));
    }

    @GetMapping("/blog/years")
    public Result<List<Integer>> getYears() {
        return Result.success(() -> blogService.getYears());
    }

    @GetMapping("/blog/count")
    public Result<Long> count() {
        return Result.success(() -> blogService.count());
    }

    @PostMapping("/blog/ids")
    public Result<List<Long>> findIds(@RequestBody Pageable pageRequest) {
        return Result.success(() -> blogService.findIds(pageRequest));
    }

    @GetMapping("/blog/{blogId}")
    public Result<Void> setReadCount(@PathVariable Long blogId) {
        return Result.success(() -> blogService.setReadCount(blogId));
    }

    @GetMapping("/blog/status/{blogId}")
    public Result<Integer> findStatusById(@PathVariable Long blogId) {
        return Result.success(() -> blogService.findStatusById(blogId));
    }

    @PostMapping("/blog/page")
    public Result<Page<BlogEntityRpcVo>> findPage(@RequestBody PageRequest pageRequest) {
        return Result.success(() -> blogService.findPage(pageRequest));
    }

    @PostMapping("/blog/page/year/{start}/{end}")
    public Result<Page<BlogEntityRpcVo>> findPageByCreatedBetween(@RequestBody PageRequest pageRequest,
                                                                  @PathVariable(value = "start") LocalDateTime start,
                                                                  @PathVariable(value = "end") LocalDateTime end) {
        return Result.success(() -> blogService.findPageByCreatedBetween(pageRequest, start, end));
    }

    @GetMapping("/blog/count/{start}/{end}")
    public Result<Long> countByCreatedBetween(@PathVariable(value = "start") LocalDateTime start,
                                              @PathVariable(value = "end") LocalDateTime end) {
        return Result.success(() -> blogService.countByCreatedBetween(start, end));
    }

    @GetMapping("/blog/page/count/year/{created}/{start}/{end}")
    public Result<Long> getPageCountYear(@PathVariable(value = "created") LocalDateTime created,
                                         @PathVariable(value = "start") LocalDateTime start,
                                         @PathVariable(value = "end") LocalDateTime end) {
        return Result.success(() -> blogService.getPageCountYear(created, start, end));
    }

    @GetMapping("/blog/count/until/{created}")
    public Result<Long> countByCreatedGreaterThanEqual(LocalDateTime created) {
        return Result.success(() -> blogService.countByCreatedGreaterThanEqual(created));
    }
}