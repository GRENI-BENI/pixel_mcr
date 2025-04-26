package com.vady.commentservice.feign;

import com.vady.commentservice.dto.UserExtendedDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "iam-service")
public interface UserFeignClient {
    
    @GetMapping("/user/batch-get")
    ResponseEntity<List<UserExtendedDto>> getUsersByIds(@RequestParam Set<String> userIds);
}