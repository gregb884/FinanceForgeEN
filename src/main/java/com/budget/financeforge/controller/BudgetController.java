package com.budget.financeforge.controller;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.BudgetDto;
import com.budget.financeforge.dto.BudgetSavingsUpdateDTO;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Controller
public class BudgetController {



   private final  BudgetService budgetService;


   private final SalaryService salaryService;


   private final ExchangeRateService exchangeRateService;


   private final SettingsService settingsService;

   private final TransactionService transactionService;


    public BudgetController(BudgetService budgetService, SalaryService salaryService, ExchangeRateService exchangeRateService, SettingsService settingsService, TransactionService transactionService) {
        this.budgetService = budgetService;
        this.salaryService = salaryService;
        this.exchangeRateService = exchangeRateService;
        this.settingsService = settingsService;
        this.transactionService = transactionService;
    }

    @GetMapping("/panel")
    String getBudgets(@AuthenticationPrincipal User user, ModelMap model,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page,size, Sort.by("startDate").descending());

        Page<Budget> budgetPage = budgetService.findBudgetPaginated(user,pageable);

        model.put("user", user);
        model.put("budgets",budgetPage);
        model.put("budgetSize", budgetService.getBudgets(user).size());


        return "panel";
    }

    @GetMapping("/budget/{budgetId}")
    String singleBudgetView(@AuthenticationPrincipal User user,
                            @PathVariable Long budgetId,
                            ModelMap model) throws AccessDeniedException {

        Budget budget = budgetService.findOne(budgetId, user);

        BudgetSavingsUpdateDTO budgetSavingsUpdateDTO = new BudgetSavingsUpdateDTO();

        BigDecimal totalSalary = salaryService.totalSalary(budgetId, user);

        BigDecimal rest = totalSalary.subtract(budgetService.totalPlanned(budgetId, user));

        Set<Transaction> transactions = new TreeSet<>(budget.getTransactions());

        budget.setTransactions(transactions);

        Set<Group> groupSet = new TreeSet<>(budget.getGroups());

        budget.setGroups(groupSet);

        Set<Category> categories = new TreeSet<>(budget.getGroups().stream().filter(group -> group.getName().equals("Ground Cost")).findFirst().map(Group::getCategories).orElse(Collections.emptySet()));

        categories.addAll(budget.getGroups().stream().filter(group -> group.getName().equals("Additional")).findFirst().map(Group::getCategories).orElse(Collections.emptySet()));

        List<Map<String, Object>> creditData = new ArrayList<>();

        BigDecimal totalSpend = transactionService.sumTotalForBudget(budgetId);
        if (totalSpend == null) {
            totalSpend = BigDecimal.ZERO;
        }


        BigDecimal balance = budget.getTotal().subtract(totalSpend);

        for (Credit credit: budget.getCredits()){

            Map<String, Object> creditMap = new HashMap<>();
            creditMap.put("name", credit.getName());
            creditMap.put("payment", credit.getPayment());
            creditMap.put("TotalParts", credit.getTotalParts());
            creditMap.put("ActualParts", credit.getActualPart());
            creditData.add(creditMap);
        }

        BigDecimal restToSpend = budgetService.restToSpendMoney(budgetId, user).setScale(2, RoundingMode.DOWN);


       BigDecimal eurToPln = exchangeRateService.getBidValueForPln("eur");

       Settings settings = settingsService.findOne(user.getId());

        model.put("creditData", creditData);

        model.put("restToSpend", restToSpend);

        model.put("balanceAfterSpend", balance.subtract(restToSpend).setScale(2, RoundingMode.DOWN));

        model.put("chartSpend", budgetService.chartSpendView(budget));

        model.put("eurToPln", eurToPln);

        model.put("categories" , categories);

        model.put("rest", rest);

        model.put("balance", balance);

        model.put("totalSalary", totalSalary);

        model.put("user", user);

        model.put("budget" , budget);

        model.put("budgetSavingsDto", budgetSavingsUpdateDTO);

        model.put("settings", settings);

        return "budget";
    }

    @PostMapping("/panel")
    String goToBudgetView(@AuthenticationPrincipal User user){

        return "redirect:/createNewBudget";
    }

    @GetMapping("/createNewBudget")
    String newBudgetView(@AuthenticationPrincipal User user, ModelMap model){

        Budget budget = new Budget();
        model.put("user", user);
        model.put("budget", budget);

        return "createNewBudget";
    }

    @PostMapping("/createNewBudget")
    String newBudget(@AuthenticationPrincipal User user, ModelMap model,
                     @ModelAttribute BudgetDto budgetDto) throws AccessDeniedException {

        if (!budgetService.getBudgets(user).isEmpty())

        {

          Budget budget =  budgetService.saveBudget(user,budgetDto);

          return dataTransferForNewBudgets(user,model,budget.getId(),0,5);
        }

        Budget budget =  budgetService.saveBudget(user,budgetDto);


        return singleBudgetView(user,budget.getId(),model);
    }


    @GetMapping("/budget/{budgetId}/transferData")
    public String dataTransferForNewBudgets(@AuthenticationPrincipal User user, ModelMap model,
                                            @PathVariable Long budgetId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size){

        Pageable pageable = PageRequest.of(page,size, Sort.by("startDate").descending());

        Page<Budget> budgetPage = budgetService.findBudgetPaginatedWithoutLastMonth(user,pageable);

        model.put("user", user);
        model.put("budgets",budgetPage);
        model.put("budgetSize", budgetService.getBudgets(user).size());
        model.put("budgetId", budgetId);

        return "fragment/DataCopy";

    }

    @GetMapping("/budget/{newBudgetId}/transferData/{oldBudgetId}")
    public String dataTransfer(@AuthenticationPrincipal User user,
                              @PathVariable Long newBudgetId,
                              @PathVariable Long oldBudgetId) throws AccessDeniedException {


        budgetService.transferDataFromAnotherBudget(newBudgetId,oldBudgetId,user);


        return "redirect:/budget/" + newBudgetId;

    }

    @PostMapping("/budget/{budgetId}/savings")
    public String updateSavings(@PathVariable Long budgetId,
                                @AuthenticationPrincipal User user,
                                @ModelAttribute BudgetSavingsUpdateDTO budgetSavingsUpdateDTO) throws AccessDeniedException {

     Budget budget = budgetService.findOne(budgetId, user);


     budget.setSavings(budgetSavingsUpdateDTO.getSavings());

     budgetService.updateSavings(budget);

     return "redirect:/budget/"+budgetId;
    }


    @DeleteMapping("/budget/{budgetId}/delete")
    @ResponseBody
    public String deleteTransaction(@PathVariable Long budgetId,
                                    @AuthenticationPrincipal User user) throws AccessDeniedException {



        budgetService.delete(budgetId,user);


        return "redirect:/budget/"+ budgetId + "/transaction" ;


    }
    

}
