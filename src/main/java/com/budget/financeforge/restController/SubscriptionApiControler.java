package com.budget.financeforge.restController;

import com.budget.financeforge.domain.Subscription;
import com.budget.financeforge.domain.Transaction;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionApiControler {


   private final SubscriptionService subscriptionService;

    public SubscriptionApiControler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @GetMapping("/dataBudget")
    public ResponseEntity<Page<Subscription>> getSubscriptionDataForBudget(
            @RequestParam Long budgetId,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,desc") String sort) throws AccessDeniedException {


        Page<Subscription> pageResult = subscriptionService.getSubscriptionForBudget(budgetId,search,sort,page,size, user);

        return ResponseEntity.ok(pageResult);
    }


    @PutMapping("/{budgetId}/{subscriptionId}/isPaid")
    public void IsPaidCheckBox(@PathVariable Long subscriptionId,
                               @PathVariable Long budgetId,
                               @AuthenticationPrincipal User user) throws AccessDeniedException {

        Subscription subscription =  subscriptionService.findOne(subscriptionId, user);

        subscriptionService.addPaidToBudget(subscription, budgetId, user);

    }

    @PutMapping("/{budgetId}/{subscriptionId}/isNotPaid")
    public void IsNotPaidCheckBox(@PathVariable Long subscriptionId,
                                  @AuthenticationPrincipal User user) throws AccessDeniedException {

        Subscription subscription =  subscriptionService.findOne(subscriptionId, user);



        subscriptionService.deletePaidToBudget(subscription, user);

    }
}
