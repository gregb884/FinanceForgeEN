package com.budget.financeforge.controller;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.service.BudgetService;
import com.budget.financeforge.service.CategoryService;
import com.budget.financeforge.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.TreeSet;

@Controller
@RequestMapping("/budget/{budgetId}/group")
public class GroupController {


   private final GroupService groupService;



   private final BudgetService budgetService;

    public GroupController(GroupService groupService, BudgetService budgetService) {
        this.groupService = groupService;
        this.budgetService = budgetService;
    }


    @GetMapping("{groupId}")
    String groupView(@AuthenticationPrincipal User user,
                         @PathVariable Long groupId,
                         ModelMap model) throws AccessDeniedException {

        Group group = groupService.findOne(groupId, user);
        Category category = new Category();
        Salary salary = new Salary();
        Set<Category> categorySet = new TreeSet<>(group.getCategories());
        group.setCategories(categorySet);
        Set<Salary> salaries = new TreeSet<>(group.getBudget().getSalaries());
        group.getBudget().setSalaries(salaries);

        model.put("user", user);
        model.put("group", group);
        model.put("category", category);
        model.put("salary", salary);

        return "group";
    }

    @GetMapping("/addNewGroup")
    String addNewGroup(@PathVariable Long budgetId,
                       @AuthenticationPrincipal User user) throws AccessDeniedException {


        Group group = new Group();

        group.setName("Additional");

        Budget budget = budgetService.findOne(budgetId, user);

        group.setBudget(budget);

        Set<Group> groupSet = budget.getGroups();

        groupSet.add(group);

        budget.setGroups(groupSet);

        groupService.save(group);

        return "redirect:/budget/" + budgetId + "/group/" + group.getId();

    }

    @GetMapping("{groupid}/category/undefined")
    String redirectionToAddCategory(@PathVariable Long groupid,
                                    @PathVariable Long budgetId){


        return "redirect:/budget/" + budgetId + "/group/" + groupid;
    }


}
