package org.chiu.micro.blog.rpc;

import org.chiu.micro.blog.dto.UserEntityDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface UserHttpService {
  
    @GetExchange("/user/{userId}")
    UserEntityDto findById(@PathVariable Long userId);
}
