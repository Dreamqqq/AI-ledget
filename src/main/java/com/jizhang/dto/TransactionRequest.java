package com.jizhang.dto;

import com.jizhang.enums.TransactionType;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "账单类型不能为空")
    private TransactionType type;

    @NotBlank(message = "类目不能为空")
    @Size(max = 50, message = "类目长度不能超过50个字符")
    private String category;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    private BigDecimal amount;

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;

    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;
}
