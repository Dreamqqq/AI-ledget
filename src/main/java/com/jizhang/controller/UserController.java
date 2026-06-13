package com.jizhang.controller;

import com.jizhang.common.Result;
import com.jizhang.dto.StatisticsResponse;
import com.jizhang.dto.UpdateProfileRequest;
import com.jizhang.entity.User;
import com.jizhang.enums.ErrorCode;
import com.jizhang.exception.BusinessException;
import com.jizhang.repository.UserRepository;
import com.jizhang.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ApiOperation("获取记账统计")
    @GetMapping("/statistics")
    public Result<StatisticsResponse> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        StatisticsResponse statistics = userService.getStatistics(userId);
        return Result.success(statistics);
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<Map<String, Object>> users = userPage.getContent().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("phone", user.getPhone());
                    userMap.put("name", user.getName());
                    userMap.put("age", user.getAge());
                    userMap.put("occupation", user.getOccupation());
                    userMap.put("gender", user.getGender());
                    userMap.put("createdAt", user.getCreatedAt());
                    return userMap;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("totalElements", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("currentPage", page);
        
        return Result.success(response);
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    public Result<String> deleteUser(HttpServletRequest request, @PathVariable Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        
        if (!currentUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        userService.deleteUser(userId);
        return Result.success("删除成功", "删除成功");
    }
}
