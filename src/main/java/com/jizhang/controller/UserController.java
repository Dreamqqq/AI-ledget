package com.jizhang.controller;

import com.jizhang.common.Result;
import com.jizhang.dto.UpdateProfileRequest;
import com.jizhang.entity.User;
import com.jizhang.enums.ErrorCode;
import com.jizhang.exception.BusinessException;
import com.jizhang.repository.UserRepository;
import com.jizhang.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @ApiOperation("获取个人信息")
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("phone", user.getPhone());
        profile.put("name", user.getName());
        profile.put("age", user.getAge());
        profile.put("occupation", user.getOccupation());
        profile.put("gender", user.getGender());
        
        return Result.success(profile);
    }

    @ApiOperation("修改个人信息")
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        
        User user = userService.updateProfile(userId, updateRequest);
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("phone", user.getPhone());
        profile.put("name", user.getName());
        profile.put("age", user.getAge());
        profile.put("occupation", user.getOccupation());
        profile.put("gender", user.getGender());
        
        return Result.success("修改成功", profile);
    }
}
