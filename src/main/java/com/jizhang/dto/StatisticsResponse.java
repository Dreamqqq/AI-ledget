package com.jizhang.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("统计信息响应")
public class StatisticsResponse {
    
    @ApiModelProperty("记账总次数")
    private Long totalTransactions;
    
    @ApiModelProperty("总收入")
    private BigDecimal totalIncome;
    
    @ApiModelProperty("总支出")
    private BigDecimal totalExpense;
}
