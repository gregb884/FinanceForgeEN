package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Category;
import com.budget.financeforge.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public interface BudgetRepository extends PagingAndSortingRepository<Budget, Long> {


    TreeSet<Budget> findByUsersIn(Set<User> users);

    Page<Budget> findBudgetByUsers(User user, Pageable pageable);

    long countByUsersIn(Set<User> users);

    Optional<Budget> findById(Long budgetId);

    Budget save(Budget budget1);

    void deleteById(Long budgetId);

    @Query("SELECT COUNT(DISTINCT t.category.id) FROM Transaction t WHERE t.budget.id = :budgetId")
    int countUniqueCategoriesByBudgetId(@Param("budgetId") Long budgetId);

}
