package com.budget.financeforge.restController;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Category;
import com.budget.financeforge.domain.Transaction;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.repository.TransactionRepository;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.CategoryService;
import com.budget.financeforge.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionApiController {


   private final TransactionService transactionService;

    public TransactionApiController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/dataBudget")
    public ResponseEntity<Page<Transaction>> getTransactionsDataForBudget(
            @RequestParam Long budgetId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,desc") String sort) {


        Page<Transaction> pageResult = transactionService.getTransactionForBudget(budgetId,search,sort,page,size);

        return ResponseEntity.ok(pageResult);
    }



    @GetMapping("/dataCategory")
    public ResponseEntity<Page<Transaction>> getTransactionsDataForCategory(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,asc") String sort) {


        Page<Transaction> pageResult = transactionService.getTransactionForCategory(categoryId,search,sort,page,size);

        return ResponseEntity.ok(pageResult);
    }



}