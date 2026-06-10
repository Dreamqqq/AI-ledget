package com.jizhang.repository;

import com.jizhang.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
