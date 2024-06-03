package com.budget.financeforge.service;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.enumpaket.Currency;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.Set;


@Service
public class SubscriptionService {


    private final BudgetService budgetService;

    private final SubscriptionRepository subscriptionRepository;

    private final TransactionService transactionService;

    private final CategoryService categoryService;


    private final ExchangeRateService exchangeRateService;

    public SubscriptionService(BudgetService budgetService, SubscriptionRepository subscriptionRepository, TransactionService transactionService, CategoryService categoryService, ExchangeRateService exchangeRateService) {
        this.budgetService = budgetService;
        this.subscriptionRepository = subscriptionRepository;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.exchangeRateService = exchangeRateService;
    }


    public Subscription save(Subscription subscription, Long budgetId, User user) throws AccessDeniedException {

        Budget budget = budgetService.findOne(budgetId, user);

        Subscription subscription1 = new Subscription();
        subscription1.setBudget(budget);
        subscription1.setName(subscription.getName());
        subscription1.setTotal(subscription.getTotal());
        subscription1.setPaid(subscription.isPaid());
        subscription1.setCurrency(subscription.getCurrency());



        return subscriptionRepository.save(subscription1);

    }

    public Page<Subscription> getSubscriptionForBudget(Long budgetId, String search, String sort, int page, int size, User user) throws AccessDeniedException {

        Budget budget = budgetService.findOne(budgetId, user);


       return subscriptionRepository.findByBudgetIdAndNameContainingIgnoreCase(budgetId,search,getPageable(page,size,sort));

    }


    public Pageable getPageable(int page, int size, String sort){

        String[] sortParams = sort.split(",");
        String fieldName = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1] : "asc";
        Sort sortOrder = Sort.by(Sort.Order.by(fieldName).with(
                Sort.Direction.fromString(direction)
        ));

        return PageRequest.of(page, size, sortOrder);

    }

    public Subscription findOne(Long subscriptionId, User user) throws AccessDeniedException {


        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        if (subscription.getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }


       return subscriptionRepository.findById(subscriptionId).orElse(null);


    }

    public Subscription edit(Long subscriptionId, Subscription subscription, User user) throws AccessDeniedException {

        Subscription subscription1 = subscriptionRepository.findById(subscriptionId).orElse(null);

        Budget budget = subscription1.getBudget();

        Set<Category> categories = budget.getGroups()
                .stream().filter(group ->
                        group.getName().equals("Ground Cost")).findFirst().get().getCategories();

        Category category = categories.stream().filter(category1 -> category1.getName().equals("Subscription")).findFirst().get();

        category.setPlanned(category.getPlanned().subtract(subscription1.getTotal()));



        if (subscription.isPaid() && !subscription1.isPaid())
        {
            addTransactionPaid(subscription,budget);
        }

        if(!subscription.isPaid() && subscription1.isPaid()){

            Long transactionId = transactionService.findTransactionIdByNote("Subscription paid : "+ subscription.getName(), budget.getId()).getId();

            transactionService.delete(transactionId,user);
        }

        if(subscription.getTotal().compareTo(subscription1.getTotal()) != 0 && subscription.isPaid() && subscription1.isPaid() ){

            Long transactionId = transactionService.findTransactionIdByNote("Subscription paid : "+ subscription.getName(), budget.getId()).getId();

            transactionService.delete(transactionId,user);

            addTransactionPaid(subscription,budget);

        }

            subscription1.setName(subscription.getName());
            subscription1.setTotal(subscription.getTotal());
            subscription1.setPaid(subscription.isPaid());
            subscription1.setCurrency(subscription.getCurrency());

        category.setPlanned(category.getPlanned().add(subscription.getTotal()));

        CategoryDto categoryDto = new CategoryDto(category.getPlanned(), category.getName());

        categoryService.updateCategory(categoryDto, category.getId(), user);



       return subscriptionRepository.save(subscription1);

    }


    public void createCategorySubscription(Long budgetId, Subscription subscription, User user) throws AccessDeniedException {


        Budget budget = budgetService.findOne(budgetId, user);

        Long groupId = budget.getGroups().stream().filter(group -> "Ground Cost".equals(group.getName()))
                .findFirst().get().getId();

        Set<Category> categories = budget.getGroups().stream().filter(group -> "Ground Cost".equals(group.getName()))
                .findFirst().map(Group::getCategories)
                .orElseThrow(() -> new RuntimeException("Group not find"));

        boolean hasSubscriptionCategory = categories.stream().anyMatch(category -> "Subscription".equals(category.getName()));

        if(!hasSubscriptionCategory){

            Category category = new Category();

            category.setName("Subscription");

            if (!subscription.getCurrency().equals(budget.getCurrency()))

            {

                switch (subscription.getCurrency()){

                    case PLN -> category.setPlanned(subscription.getTotal().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP));

                    case EUR -> category.setPlanned(subscription.getTotal().multiply(exchangeRateService.getBidValueForPln("EUR")));

                }

            }

            else {


                category.setPlanned(subscription.getTotal());

            }


            category.setCurrency(budget.getCurrency());

            CategoryDto categoryDto = new CategoryDto(category.getPlanned(), category.getName());

            categoryService.save(categoryDto, groupId, user);
        }

        else {

            updatePlanned(subscription,budget, user);
        }


        if(subscription.isPaid())
        {

            addTransactionPaid(subscription,budget);

        }

    }

    public void addTransactionPaid(Subscription subscription, Budget budget){

        Transaction transaction = new Transaction();

        transaction.setNote("Subscription paid : "+ subscription.getName());
        transaction.setTotal(subscription.getTotal());
        transaction.setCurrency(subscription.getCurrency());
        transaction.setBudget(budget);
        transaction.setCategory(budget.getGroups().stream()
                .filter(group -> "Ground Cost".equals(group.getName()))
                .findFirst().get().getCategories().stream().
                filter(category -> "Subscription".equals(category.getName()))
                .findFirst().get());

        transactionService.SaveTransaction(transaction);

    }

    public void addPaidToBudget(Subscription subscription, Long budgetId, User user) throws AccessDeniedException {


       Budget budget = budgetService.findOne(budgetId, user);


        addTransactionPaid(subscription,budget);

        subscription.setPaid(true);

        edit(subscription.getId(), subscription, user);


    }


    public void deletePaidToBudget(Subscription subscription, User user) throws AccessDeniedException {

        subscription.setPaid(false);
        edit(subscription.getId(), subscription,user);

        Long transactionId = transactionService.findTransactionIdByNote("Subscription paid : "+ subscription.getName(), subscription.getBudget().getId()).getId();

        transactionService.delete(transactionId,user);

    }

    public void updatePlanned(Subscription subscription, Budget budget, User user) throws AccessDeniedException {


        Set<Category> categories = budget.getGroups()
                .stream().filter(group ->
                        group.getName().equals("Ground Cost")).findFirst().get().getCategories();


        Category category = categories.stream().filter(category1 -> category1.getName().equals("Subscription")).findFirst().get();

        if (!subscription.getCurrency().equals(budget.getCurrency()))

        {

            switch (subscription.getCurrency()){

                case PLN -> category.setPlanned(category.getPlanned().add(subscription.getTotal().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP)));

                case EUR -> category.setPlanned(category.getPlanned().add(subscription.getTotal().multiply(exchangeRateService.getBidValueForPln("EUR"))));

            }

        }

        else {


            category.setPlanned(category.getPlanned().add(subscription.getTotal()));

        }

        CategoryDto categoryDto = new CategoryDto(category.getPlanned(),category.getName());


        categoryService.updateCategory(categoryDto, category.getId(), user);

    }

    public void delete(Long subscriptionId, User user) throws AccessDeniedException {



        findOne(subscriptionId,user);

        subscriptionRepository.deleteById(subscriptionId);


    }
}
