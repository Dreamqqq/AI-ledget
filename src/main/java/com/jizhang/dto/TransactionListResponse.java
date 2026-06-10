package com.jizhang.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TransactionListResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<TransactionResponse> transactions;

    public TransactionListResponse(BigDecimal totalIncome, BigDecimal totalExpense, List<TransactionResponse> transactions) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.transactions = transactions;
    }
}
