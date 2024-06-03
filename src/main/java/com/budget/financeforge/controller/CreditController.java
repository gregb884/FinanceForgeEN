package com.budget.financeforge.controller;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Credit;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.CreditDto;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.CreditService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.TreeSet;

@Controller
@RequestMapping("/budget/{budgetId}/credit")
public class CreditController {


   private final BudgetService budgetService;


   private final CreditService creditService;

    public CreditController(BudgetService budgetService, CreditService creditService) {
        this.budgetService = budgetService;
        this.creditService = creditService;
    }


    @GetMapping("{creditId}")
    public String editCredit(@PathVariable Long budgetId,
                             @PathVariable Long creditId,
                             @AuthenticationPrincipal User user,
                             ModelMap model) throws AccessDeniedException {

        Credit credit = creditService.findOne(creditId, user);

        model.put("user", user);
        model.put("credit", credit);

        return "fragment/editCredit";
    }


    @GetMapping("")
    public String creditView(@PathVariable Long budgetId,
                             @AuthenticationPrincipal User user,
                             ModelMap model) throws AccessDeniedException {

        Credit credit = new Credit();
        Budget budget = budgetService.findOne(budgetId, user);

        budget.setCredits(new TreeSet<>(budget.getCredits()));


        model.put("user", user);
        model.put("credit",credit);
        model.put("budget", budget);

        return "credit";
    }

    @PostMapping("/addcredit")
    public String addCredit(@PathVariable Long budgetId,
                            @AuthenticationPrincipal User user,
                            @ModelAttribute CreditDto creditDto) throws AccessDeniedException {

        creditService.saveCredit(creditDto ,budgetId, user);



        return "redirect:/budget/"+budgetId+"/credit";
    }

    @PostMapping("{creditId}")
    public String addCredit(@PathVariable Long budgetId,
                            @PathVariable Long creditId,
                            @AuthenticationPrincipal User user,
                            @ModelAttribute CreditDto creditDto) throws AccessDeniedException {


        creditService.editCredit(creditDto, creditId, user);




        return "redirect:/budget/"+budgetId+"/credit/"+ creditId ;
    }

    @PostMapping("{creditId}/repaid")
    public String nextMonth(@PathVariable Long budgetId,
                            @PathVariable Long creditId,
                            @AuthenticationPrincipal User user,
                            ModelMap model,
                            RedirectAttributes redirectAttributes) throws AccessDeniedException {

       int actual =  creditService.findOne(creditId,user).getActualPart();

       int after =  creditService.nextMonth(creditId, user).getActualPart();

       if(actual == after){

           redirectAttributes.addFlashAttribute("Closed", "Credit Closed !");
       }


        return "redirect:/budget/"+budgetId+"/credit";
    }


    @DeleteMapping("{creditId}/delete")
    @ResponseBody
    public String deleteCredit(@PathVariable Long budgetId,
                               @PathVariable Long creditId,
                               @AuthenticationPrincipal User user) throws AccessDeniedException {


        creditService.deleteCredit(creditId,user);



        return "redirect:/budget/"+ budgetId +"/credit/" + creditId;

    }





}
