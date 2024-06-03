package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Category;
import com.budget.financeforge.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {


    Optional<Transaction> findById(Long transactionId);

    Transaction save(Transaction transaction1);


    Page<Transaction> findByBudgetIdAndNoteContainingIgnoreCase(
            Long budgetId,
            String search,
            Pageable pageable
    );

    Page<Transaction> findByCategoryIdAndNoteContainingIgnoreCase(
            Long categoryId,
            String search,
            Pageable pageable
    );

    Optional<Transaction> findByNoteContainingAndBudgetId(String name, Long budgetId);


    void deleteById(Long transactionId);


    @Query("SELECT SUM(t.total) FROM Transaction t WHERE t.budget.id = :budgetId")
    BigDecimal sumTotalByBudgetId(@Param("budgetId") Long budgetId);


}
