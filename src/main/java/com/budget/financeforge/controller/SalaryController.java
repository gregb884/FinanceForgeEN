package com.budget.financeforge.controller;

import com.budget.financeforge.domain.Salary;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.SalaryDto;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@Controller
@RequestMapping("/budget/{budgetId}/group")
public class SalaryController {



   private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @PostMapping("/addSalary")
    public String addSalary(@ModelAttribute SalaryDto salaryDto,
                              @AuthenticationPrincipal User user,
                              @PathVariable Long budgetId) throws AccessDeniedException {


        salaryService.saveSalary(salaryDto,budgetId, user);


        return "redirect:/budget/" + budgetId;

    }

    @DeleteMapping("{salaryId}/delete")
    @ResponseBody
    public String deleteSalary(@PathVariable Long budgetId,
                               @PathVariable Long salaryId,
                               @AuthenticationPrincipal User user) throws AccessDeniedException {

        salaryService.deleteSalary(salaryId,user);



        return "redirect:/panel";
    }


}
