package com.jizhang.controller;

import com.jizhang.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "类目接口")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final List<String> INCOME_CATEGORIES = Arrays.asList(
            "工资收入", "兼职收入", "投资收益", "红包收入", "其他收入"
    );

    private static final List<String> EXPENSE_CATEGORIES = Arrays.asList(
            "餐饮美食", "交通出行", "购物消费", "娱乐休闲", 
            "生活缴费", "医疗健康", "学习教育", "其他支出"
    );

    @ApiOperation("获取类目列表")
    @GetMapping
    public Result<Map<String, List<String>>> getCategories() {
        Map<String, List<String>> categories = new HashMap<>();
        categories.put("income", INCOME_CATEGORIES);
        categories.put("expense", EXPENSE_CATEGORIES);
        
        return Result.success(categories);
    }
}
