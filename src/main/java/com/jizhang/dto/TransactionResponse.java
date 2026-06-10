package com.jizhang.dto;

import com.jizhang.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    private String remark;
    private String imageUrl;
}
