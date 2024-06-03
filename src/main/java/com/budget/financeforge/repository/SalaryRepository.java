package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {


    void deleteById(Long salaryId);

}
