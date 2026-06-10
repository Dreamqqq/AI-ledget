package com.jizhang.controller;

import com.jizhang.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "健康检查")
@RestController
@RequestMapping("/api")
public class HealthController {

    @ApiOperation("健康检查接口")
    @GetMapping("/health")
    public Result<Map<String, String>> health() {
        Map<String, String> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "AI记账本系统运行正常");
        return Result.success(data);
    }
}
