package com.jizhang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图片识别结果")
public class OcrResponse {

    @Schema(description = "金额", example = "123.45")
    private BigDecimal amount;

    @Schema(description = "日期", example = "2026-06-10")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "商家/备注", example = "肯德基")
    private String merchant;

    @Schema(description = "类目", example = "餐饮美食")
    private String category;
}
