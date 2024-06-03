package com.budget.financeforge.service;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.dto.CreditDto;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.repository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

@Service
public class CreditService {



    private final CreditRepository creditRepository;


    private final BudgetService budgetService;


    private final CategoryService categoryService;


    private final TransactionService transactionService;


    private final ExchangeRateService exchangeRateService;

    public CreditService(CreditRepository creditRepository, BudgetService budgetService, CategoryService categoryService, TransactionService transactionService, ExchangeRateService exchangeRateService) {
        this.creditRepository = creditRepository;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
        this.exchangeRateService = exchangeRateService;
    }

    public Credit findOne(Long creditId, User user) throws AccessDeniedException {


        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        if (credit.getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }

        return creditRepository.findById(creditId).orElse(null);

    }


    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public Credit saveCredit(CreditDto creditDto , Long budgetId, User user) throws AccessDeniedException {


        Budget budget = budgetService.findOne(budgetId, user);

        Credit credit1 = new Credit();


        int totalPart = (int) ChronoUnit.MONTHS.between(convertToLocalDateTimeViaInstant(creditDto.getStartDate()),
                convertToLocalDateTimeViaInstant(creditDto.getEndDate()));

        int actualPart = (int) ChronoUnit.MONTHS.between(convertToLocalDateTimeViaInstant(creditDto.getStartDate()),
                LocalDateTime.now());

        totalPart++;
        actualPart++;

        credit1.setBudget(budget);
        credit1.setName(creditDto.getName());
        credit1.setStartDate(creditDto.getStartDate());
        credit1.setEndDate(creditDto.getEndDate());
        credit1.setTotal(creditDto.getTotal());
        credit1.setTotalParts(totalPart);
        credit1.setActualPart(actualPart);
        credit1.setCurrency(creditDto.getCurrency());
        credit1.setPayment(creditDto.getPayment());
        credit1.setRepaid(new BigDecimal(actualPart).multiply(creditDto.getPayment()));

        Set<Category> categories = budget.getGroups().stream().
                filter(group -> group.getName().equals("Ground Cost")).
                findFirst().get().getCategories();

        boolean hasCreditCategory = categories.stream().anyMatch(category -> "Credit".equals(category.getName()));


        if (!hasCreditCategory) {

            Category category = new Category();

            category.setName("Credit");

            if(!creditDto.getCurrency().equals(budget.getCurrency()))
            {

                {

                    switch (creditDto.getCurrency()){

                        case PLN -> category.setPlanned(creditDto.getPayment().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP));

                        case EUR -> category.setPlanned(creditDto.getPayment().multiply(exchangeRateService.getBidValueForPln("EUR")));

                    }

                }


            }  else {

                category.setPlanned(creditDto.getPayment());
            }

            CategoryDto categoryDto = new CategoryDto(category.getPlanned(), category.getName());


            categoryService.save(categoryDto, budget.getGroups().stream()
                    .filter(group -> "Ground Cost".equals(group.getName())).findFirst().get().getId(), user);
            Transaction transaction = new Transaction();
            transaction.setDate(DateUtils.createNow().getTime());
            transaction.setBudget(credit1.getBudget());
            transaction.setCurrency(credit1.getCurrency());
            transaction.setNote(credit1.getName() + " " + credit1.getActualPart()+"/"+credit1.getTotalParts());
            transaction.setTotal(credit1.getPayment());
            transaction.setCategory(categoryService.findCategoryIdByName("Credit", user, budgetId));
            transactionService.SaveTransaction(transaction);
            return creditRepository.save(credit1);
        }

        Category category = categoryService.findCategoryIdByName("Credit", user, budgetId);

        BigDecimal planned = category.getPlanned();

        if(!creditDto.getCurrency().equals(budget.getCurrency()))
        {

            {

                switch (creditDto.getCurrency()){

                    case PLN -> category.setPlanned(creditDto.getPayment().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP));

                    case EUR -> category.setPlanned(creditDto.getPayment().multiply(exchangeRateService.getBidValueForPln("EUR")));

                }

            }


        }  else {

            category.setPlanned(planned.add(creditDto.getPayment()));
        }


        categoryService.updateCreditCategory(category);

        return creditRepository.save(credit1);


    }

    public Credit editCredit(CreditDto creditDto , Long creditId, User user) throws AccessDeniedException {

        Credit credit1 = findOne(creditId, user);

        credit1.setName(creditDto.getName());
        credit1.setStartDate(creditDto.getStartDate());
        credit1.setEndDate(creditDto.getEndDate());
        credit1.setTotal(creditDto.getTotal());
        credit1.setTotalParts(creditDto.getTotalParts());
        credit1.setActualPart(creditDto.getActualPart());
        credit1.setCurrency(creditDto.getCurrency());
        credit1.setPayment(creditDto.getPayment());
        credit1.setRepaid(creditDto.getRepaid());

        return creditRepository.save(credit1);

    }


    public Credit nextMonth(Long creditId, User user) throws AccessDeniedException {

        Credit credit =  findOne(creditId, user);

        int actualPart = credit.getActualPart();

        if(actualPart < credit.getTotalParts()){

            credit.setActualPart(actualPart + 1);
            credit.setRepaid(new BigDecimal(actualPart).multiply(credit.getPayment()));

            Transaction transaction = new Transaction();

            transaction.setDate(DateUtils.createNow().getTime());
            transaction.setBudget(credit.getBudget());
            transaction.setCurrency(credit.getCurrency());
            transaction.setNote(credit.getName() + " " + credit.getActualPart()+"/"+credit.getTotalParts());
            transaction.setTotal(credit.getPayment());
            transaction.setCategory(categoryService.findCategoryIdByName("Credit", user,credit.getBudget().getId()));

            transactionService.SaveTransaction(transaction);
        }

        else {

            credit.setActualPart(actualPart);

        }



        return creditRepository.save(credit);

    }

    public void deleteCredit(Long creditId, User user) throws AccessDeniedException {


        findOne(creditId,user);

        creditRepository.deleteById(creditId);


    }
}
