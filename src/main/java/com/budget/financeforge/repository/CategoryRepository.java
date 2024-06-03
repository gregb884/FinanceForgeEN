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
import java.util.Optional;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {


    Page<Category> findByGroupId( Long groupId, Pageable pageable);


    Category save(Category category1);

    Optional<Category> findById(Long categoryId);

    void deleteById(Long id);


    @Query("SELECT SUM(c.planned) FROM Category c " +
            "JOIN c.group g " +
            "JOIN g.budget b " +
            "WHERE b.id = :budgetId")
    BigDecimal findTotalPlannedByBudgetId(@Param("budgetId") Long budgetId);
}
