package com.budget.financeforge.controller;
import com.budget.financeforge.domain.Subscription;
import com.budget.financeforge.domain.Transaction;
import com.budget.financeforge.service.SubscriptionService;
import com.budget.financeforge.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@Controller
@RequestMapping("/budget/{budgetId}/subscription")
public class SubscriptionController {


   private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @GetMapping("")
    String getSubscriptionView(@AuthenticationPrincipal User user,
                               @PathVariable Long budgetId,
                               ModelMap model){

        Subscription subscription = new Subscription();


        model.put("subscription", subscription);
        model.put("user",user);
        model.put("budgetId", budgetId);

        return "subscription";


    }

    @GetMapping("/{subscriptionId}")
    public String getsubscriptionEdit(@PathVariable Long subscriptionId,
                                  @AuthenticationPrincipal User user,
                                  ModelMap model) throws AccessDeniedException {


        Subscription subscription = subscriptionService.findOne(subscriptionId, user);

        model.put("user", user);
        model.put("subscription", subscription);

        return "fragment/editSubscription";
    }

    @PostMapping("/{subscriptionId}")
    public String postSubscriptionEdit(@PathVariable Long subscriptionId,
                                   @PathVariable Long budgetId,
                                   @AuthenticationPrincipal User user,
                                   @ModelAttribute Subscription subscription) throws AccessDeniedException {


        subscriptionService.edit(subscriptionId, subscription, user);


        return "redirect:/budget/" + budgetId + "/subscription" ;
    }




    @PostMapping("/new")
    String saveSubscription(@PathVariable Long budgetId,
                            @AuthenticationPrincipal User user,
                            @ModelAttribute Subscription subscription) throws AccessDeniedException {


        subscriptionService.save(subscription, budgetId, user);

        subscriptionService.createCategorySubscription(budgetId,subscription, user);


        return "redirect:/budget/" + budgetId + "/subscription";
    }


    @DeleteMapping("{subscriptionId}/delete")
    @ResponseBody
    public String deleteSubscription(@PathVariable Long subscriptionId,
                                 @PathVariable Long budgetId,
                                 @AuthenticationPrincipal User user) throws AccessDeniedException {



       subscriptionService.delete(subscriptionId,user);


        return "redirect:/budget/" + budgetId + "/subscription/";


    }



}
