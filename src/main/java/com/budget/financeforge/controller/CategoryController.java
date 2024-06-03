package com.budget.financeforge.controller;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.CategoryService;
import com.budget.financeforge.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.TreeSet;

@Controller
@RequestMapping("/budget/{budgetId}/group/{groupId}")
public class CategoryController {


   private final CategoryService categoryService;
   private final ExchangeRateService exchangeRateService;

   private final BudgetService budgetService;

    public CategoryController(CategoryService categoryService, BudgetService budgetService, ExchangeRateService exchangeRateService) {
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/addCategory")
    public String addCategory(@ModelAttribute CategoryDto categoryDto,
                              @AuthenticationPrincipal User user,
                              @PathVariable Long groupId,
                              @PathVariable Long budgetId) throws AccessDeniedException {


       categoryService.save(categoryDto,groupId, user);

        return "redirect:/budget/" + budgetId +"/group/"+ groupId;

    }


    @GetMapping("/category/{categoryId}")
    public String categoryView(
                               @PathVariable Long categoryId,
                               @PathVariable Long budgetId,
                               @AuthenticationPrincipal User user,
                               ModelMap model) throws AccessDeniedException {

        Transaction transaction = new Transaction();

        Category category = categoryService.findOne(categoryId, user);
        Budget budget = budgetService.findOne(budgetId, user);

        BigDecimal eurToPln = exchangeRateService.getBidValueForPln("eur");

        model.put("eurToPln", eurToPln );
        model.put("user", user);
        model.put("budget", budget);
        model.put("category", category);
        model.put("categoryName", category.getName());
        model.put("transaction", transaction);

        return "category";
    }

    @PostMapping("/category/{categoryId}")
    public String categoryEdit(@ModelAttribute CategoryDto categoryDto,
                               @PathVariable Long groupId,
                               @PathVariable Long categoryId,
                               @PathVariable Long budgetId,
                               @AuthenticationPrincipal User user) throws AccessDeniedException {



       categoryService.updateCategory(categoryDto,categoryId, user );

        return "redirect:/budget/"+ budgetId + "/group/"+groupId;
    }

    @DeleteMapping("category/{categoryId}/delete")
    @ResponseBody
    public String deleteCategory(@PathVariable Long categoryId,
                                    @PathVariable Long groupId,
                                    @PathVariable Long budgetId,
                                    @AuthenticationPrincipal User user) throws AccessDeniedException {


        categoryService.findOne(categoryId,user);

        categoryService.delete(categoryId);


        return "redirect:/budget/" + budgetId + "/group/" + groupId ;


    }


}
