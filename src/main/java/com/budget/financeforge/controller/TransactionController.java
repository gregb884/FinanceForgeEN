package com.budget.financeforge.controller;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.enumpaket.Currency;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/budget/{budgetId}/transaction")
@SessionAttributes("lastUsedCurrency")
public class TransactionController {



   private final BudgetService budgetService;

   private final TransactionService transactionService;

   private final ExchangeRateService exchangeRateService;


    public TransactionController(BudgetService budgetService, TransactionService transactionService, ExchangeRateService exchangeRateService) {
        this.budgetService = budgetService;
        this.transactionService = transactionService;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("")
    public String TransactionView(@PathVariable Long budgetId,
                                  @AuthenticationPrincipal User user,
                                  ModelMap model) throws AccessDeniedException {

        Transaction transaction = new Transaction();

        Budget budget = budgetService.findOne(budgetId, user);

        Set<Category> categorySet = budget.getGroups().stream()
                .flatMap(group -> group.getCategories().stream())
                .collect(Collectors.toCollection(TreeSet::new));


       BigDecimal eurToPln = exchangeRateService.getBidValueForPln("eur");

        model.put("eurToPln", eurToPln );
        model.put("categorySet", categorySet);
        model.put("user", user);
        model.put("budget", budget);
        model.put("transaction", transaction );

        return "transaction";

    }



    @GetMapping("/{transactionId}")
    public String TransactionEdit(
                                  @PathVariable Long transactionId,
                                  @PathVariable Long budgetId,
                                  @AuthenticationPrincipal User user,
                                  ModelMap model) throws AccessDeniedException {

        Transaction transaction = transactionService.findOne(transactionId, user);

        Budget budget = budgetService.findOne(budgetId, user);

        Set<Category> categorySet = budget.getGroups().stream()
                .flatMap(group -> group.getCategories().stream())
                .collect(Collectors.toCollection(TreeSet::new));


        model.put("user", user);
        model.put("categorySet", categorySet);
        model.put("transaction", transaction);

        return "fragment/editTransaction";
    }


    @PostMapping("/{transactionId}")
    public String TransactionUpdate(@PathVariable Long transactionId,
                                    @PathVariable Long budgetId,
                                    @ModelAttribute Transaction transaction){

        transactionService.edit(transaction,transactionId);

        return "redirect:/budget/"+ budgetId+"/transaction";

    }

    @PostMapping("/new")
    public String PostTransaction(@ModelAttribute Transaction transaction,
                                  @AuthenticationPrincipal User user,
                                  @PathVariable Long budgetId) throws AccessDeniedException {


        transaction.setBudget(budgetService.findOne(budgetId, user));

        transactionService.SaveTransaction(transaction);


        return "redirect:/budget/"+budgetId+"/transaction";
    }

    @PostMapping("/subcategory")
    public String PostTransactionForCategory(@ModelAttribute Transaction transaction,
                                             @AuthenticationPrincipal User user,
                                             @PathVariable Long budgetId) throws AccessDeniedException {

        transaction.setBudget(budgetService.findOne(budgetId, user));

        if(transaction.getTotal() == null)
        {
            return "redirect:/budget/"+budgetId+"/group/"+transaction.getCategory().getGroup().getId()+"/category/"+transaction.getCategory().getId();
        }


       Transaction transaction1 = transactionService.SaveTransaction(transaction);


        return "redirect:/budget/"+budgetId+"/group/"+transaction1.getCategory().getGroup().getId()+"/category/"+transaction1.getCategory().getId();
    }


    @DeleteMapping("/{transactionId}/delete")
    @ResponseBody
    public String deleteTransaction(@PathVariable Long transactionId,
                                    @PathVariable Long budgetId,
                                    @AuthenticationPrincipal User user) throws AccessDeniedException {



        transactionService.delete(transactionId,user);


        return "redirect:/budget/"+ budgetId + "/transaction" ;


    }



}
