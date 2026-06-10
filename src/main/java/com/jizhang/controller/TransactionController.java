package com.jizhang.controller;

import com.jizhang.common.Result;
import com.jizhang.dto.OcrRequest;
import com.jizhang.dto.OcrResponse;
import com.jizhang.dto.TransactionListResponse;
import com.jizhang.dto.TransactionRequest;
import com.jizhang.dto.TransactionResponse;
import com.jizhang.service.OcrService;
import com.jizhang.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "账单管理", description = "账单增删改查接口")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final OcrService ocrService;

    @Operation(summary = "创建账单")
    @PostMapping
    public Result<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return Result.success(response);
    }

    @Operation(summary = "获取账单列表")
    @GetMapping
    public Result<TransactionListResponse> getTransactions(
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份") @RequestParam(required = false) Integer month,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TransactionListResponse response = transactionService.getTransactions(userId, year, month);
        return Result.success(response);
    }

    @Operation(summary = "更新账单")
    @PutMapping("/{id}")
    public Result<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TransactionResponse response = transactionService.updateTransaction(userId, id, request);
        return Result.success(response);
    }

    @Operation(summary = "删除账单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTransaction(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        transactionService.deleteTransaction(userId, id);
        return Result.success(null);
    }

    @Operation(summary = "图片识别接口")
    @PostMapping("/ocr")
    public Result<OcrResponse> recognizeReceipt(@Valid @RequestBody OcrRequest request) {
        OcrResponse response = ocrService.recognizeReceipt(request.getImageUrl());
        return Result.success(response);
    }
}
