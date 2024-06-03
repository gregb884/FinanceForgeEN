package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit, Long> {


    void deleteById(Long creditId);


}
