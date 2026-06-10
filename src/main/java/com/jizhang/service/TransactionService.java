package com.jizhang.service;

import com.jizhang.dto.TransactionListResponse;
import com.jizhang.dto.TransactionRequest;
import com.jizhang.dto.TransactionResponse;
import com.jizhang.entity.Transaction;
import com.jizhang.enums.EntityType;
import com.jizhang.enums.ErrorCode;
import com.jizhang.enums.OperationType;
import com.jizhang.enums.TransactionType;
import com.jizhang.exception.BusinessException;
import com.jizhang.repository.TransactionRepository;
import com.jizhang.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OperationLogService operationLogService;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        BeanUtils.copyProperties(request, transaction);
        
        Transaction saved = transactionRepository.save(transaction);
        
        operationLogService.log(userId, OperationType.CREATE, EntityType.TRANSACTION, 
            saved.getId(), null, JsonUtil.toJson(saved));
        
        return toResponse(saved);
    }

    public TransactionListResponse getTransactions(Long userId, Integer year, Integer month) {
        List<Transaction> transactions;
        
        if (year != null && month != null) {
            transactions = transactionRepository.findByUserIdAndYearAndMonth(userId, year, month);
        } else {
            transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        }
        
        BigDecimal totalIncome = transactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalExpense = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<TransactionResponse> responseList = transactions.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return new TransactionListResponse(totalIncome, totalExpense, responseList);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
        
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        String oldValue = JsonUtil.toJson(transaction);
        
        BeanUtils.copyProperties(request, transaction);
        Transaction updated = transactionRepository.save(transaction);
        
        operationLogService.log(userId, OperationType.UPDATE, EntityType.TRANSACTION,
            id, oldValue, JsonUtil.toJson(updated));
        
        return toResponse(updated);
    }

    @Transactional
    public void deleteTransaction(Long userId, Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
        
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        String oldValue = JsonUtil.toJson(transaction);
        transactionRepository.delete(transaction);
        
        operationLogService.log(userId, OperationType.DELETE, EntityType.TRANSACTION,
            id, oldValue, null);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        BeanUtils.copyProperties(transaction, response);
        return response;
    }
}
