package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Subscription;
import com.budget.financeforge.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Long> {


    Optional<Subscription> findById(Long subscriptionId);

    Subscription save(Subscription subscription);

    Page<Subscription> findByBudgetIdAndNameContainingIgnoreCase(
            Long budgetId,
            String search,
            Pageable pageable
    );


    void deleteById(Long subscriptionId);



}
