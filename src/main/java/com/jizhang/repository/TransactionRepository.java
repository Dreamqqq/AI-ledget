package com.jizhang.repository;

import com.jizhang.entity.Transaction;
import com.jizhang.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND YEAR(t.date) = :year AND MONTH(t.date) = :month " +
           "ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndYearAndMonth(@Param("userId") Long userId,
                                                   @Param("year") int year,
                                                   @Param("month") int month);
    
    Long countByUserId(Long userId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.userId = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
}
