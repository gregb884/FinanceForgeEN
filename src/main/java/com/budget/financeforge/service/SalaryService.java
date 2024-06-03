package com.budget.financeforge.service;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Credit;
import com.budget.financeforge.domain.Salary;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.SalaryDto;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Set;

@Service
public class SalaryService {


   private final SalaryRepository salaryRepository;

    private final BudgetService budgetService;

    private final ExchangeRateService exchangeRateService;

    public SalaryService(SalaryRepository salaryRepository, BudgetService budgetService, ExchangeRateService exchangeRateService) {
        this.salaryRepository = salaryRepository;
        this.budgetService = budgetService;
        this.exchangeRateService = exchangeRateService;
    }


    public Salary saveSalary(SalaryDto salaryDto, Long budgetId, User user) throws AccessDeniedException {

        Salary salary1 = new Salary();
        Budget budget = budgetService.findOne(budgetId, user);

        salary1.setBudget(budget);

        if(!salaryDto.getCurrency().equals(budget.getCurrency()))
        {

            {

                switch (salaryDto.getCurrency()){

                    case PLN -> salary1.setTotal(salaryDto.getTotal().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP));

                    case EUR -> salary1.setTotal(salaryDto.getTotal().multiply(exchangeRateService.getBidValueForPln("EUR")));

                }

            }

            salary1.setNote(salaryDto.getNote() + " (Exchange rate: " + exchangeRateService.getBidValueForPln("EUR") +")");


        }  else {

            salary1.setTotal(salaryDto.getTotal());
            salary1.setNote(salaryDto.getNote());
        }



        salary1.setCurrency(budget.getCurrency());

        budgetService.addSalary(budgetId,salary1.getTotal());


       return salaryRepository.save(salary1);

    }

    public BigDecimal totalSalary( Long budgetId, User user) throws AccessDeniedException {

      Budget budget = budgetService.findOne(budgetId,user);

      if(budget != null) {

          Set<Salary> salaries = budget.getSalaries();

          return salaries.stream()
                  .map(Salary::getTotal)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
      } else {

          return BigDecimal.ZERO;
      }


    }

    public Salary findOne(Long salaryId, User user) throws AccessDeniedException {

        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("Salary not found"));


        if (salary.getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }


        return salaryRepository.findById(salaryId).orElse(null);

    }


    public void deleteSalary(Long salaryId, User user) throws AccessDeniedException {

      Salary salary = findOne(salaryId,user);

      budgetService.deleteSalaryFromBalance(salary.getBudget().getId(),salary.getTotal());

       salaryRepository.deleteById(salaryId);

    }
}
