package org.chiu.micro.blog.service;

import jakarta.servlet.http.HttpServletResponse;
import org.chiu.micro.blog.page.PageAdapter;
import org.chiu.micro.blog.req.BlogEntityReq;
import org.chiu.micro.blog.vo.BlogDeleteVo;
import org.chiu.micro.blog.vo.BlogEntityRpcVo;
import org.chiu.micro.blog.vo.BlogEntityVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogManagerService {

    void saveOrUpdate(BlogEntityReq blog, Long userId);

    PageAdapter<BlogEntityVo> findAllABlogs(Integer currentPage, Integer size, Long userId, List<String> roles);

    void recoverDeletedBlog(Integer idx, Long userId);

    PageAdapter<BlogDeleteVo> findDeletedBlogs(Integer currentPage, Integer size, Long userId);


    void deleteBatch(List<Long> ids, Long userId, List<String> roles);

    String uploadOss(MultipartFile image, Long userId);

    void deleteOss(String url);

    String setBlogToken(Long blogId, Long userId);

    void download(HttpServletResponse response);

    BlogEntityRpcVo findById(Long blogId);

    BlogEntityRpcVo findByIdAndUserId(Long blogId, Long userId);
}
