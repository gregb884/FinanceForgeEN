package com.budget.financeforge.restController;

import com.budget.financeforge.controller.BudgetController;
import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.BudgetSavingsUpdateDTO;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.CategoryService;
import com.budget.financeforge.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/budget")
public class BudgetApiController {


   private final BudgetService budgetService;


   private final CategoryService categoryService;


   private final BudgetController budgetController;


   private final TransactionService transactionService;

    public BudgetApiController(BudgetService budgetService, CategoryService categoryService, BudgetController budgetController, TransactionService transactionService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.budgetController = budgetController;
        this.transactionService = transactionService;
    }


    @PutMapping("/{budgetId}/transfer")
    public void transferRestToSavings(@PathVariable Long budgetId,
                                      @AuthenticationPrincipal User user) throws AccessDeniedException {


        Budget budget = budgetService.findOne(budgetId, user);

        BigDecimal totalSpend = budget.getTransactions().stream().map(Transaction::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rest = budget.getTotal().subtract(totalSpend);

        BudgetSavingsUpdateDTO budgetSavingsUpdateDTO = new BudgetSavingsUpdateDTO();

        budgetSavingsUpdateDTO.setSavings(rest.add(budget.getSavings()));

        budgetController.updateSavings(budgetId, user, budgetSavingsUpdateDTO);




        Group groupId = (budget.getGroups().stream().filter(group -> group.getName().equals("Additional")).findFirst().get());




        CategoryDto categoryDto = new CategoryDto(BigDecimal.ZERO, "Savings");


        Transaction transaction = new Transaction();

        transaction.setBudget(budget);
        transaction.setCurrency(budget.getCurrency());
        transaction.setTotal(rest);
        transaction.setNote("Transfer remaining funds to savings");
        transaction.setCategory(categoryService.save(categoryDto,groupId.getId(), user));

        transactionService.SaveTransaction(transaction);




    }



}
