package com.budget.financeforge.service;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.exchangeCurrency.ExchangeRateService;
import com.budget.financeforge.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    private final ExchangeRateService exchangeRateService;

    public TransactionService( TransactionRepository transactionRepository, ExchangeRateService exchangeRateService) {

        this.transactionRepository = transactionRepository;
        this.exchangeRateService = exchangeRateService;
    }

    public Transaction SaveTransaction(Transaction transaction){


        Transaction transaction1 = new Transaction();

        transaction1.setAddDate(DateUtils.createNow().getTime());
        transaction1.setTotal(transaction.getTotal());
        transaction1.setCategory(transaction.getCategory());
        transaction1.setCurrency(transaction.getCurrency());
        transaction1.setBudget(transaction.getBudget());
        transaction1.setNote(transaction.getNote());

        if(transaction.getDate() == null)
        {

            transaction1.setDate(DateUtils.createNow().getTime());
        }
        else {
            transaction1.setDate(transaction.getDate());
        }


        if (!transaction.getCurrency().equals(transaction.getBudget().getCurrency()))

        {

            switch (transaction.getCurrency()){

                case PLN -> transaction1.setTotal(transaction.getTotal().divide(exchangeRateService.getBidValueForPln("EUR"), 2, RoundingMode.HALF_UP));

                case EUR -> transaction1.setTotal(transaction.getTotal().multiply(exchangeRateService.getBidValueForPln("EUR")));

            }

            transaction1.setNote(transaction.getNote() + " (Exchange rate: " + exchangeRateService.getBidValueForPln("EUR") + ")");
            transaction1.setCurrency(transaction.getBudget().getCurrency());


        }



        return transactionRepository.save(transaction1);

    }


    public Transaction findOne(Long transactionId, User user) throws AccessDeniedException {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        if (transaction.getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }

        return transaction;

    }


    public Transaction edit(Transaction transaction, Long transactionId){


        Transaction transaction1 = transactionRepository.findById(transactionId).orElse(null);

        transaction1.setTotal(transaction.getTotal());
        transaction1.setCurrency(transaction.getCurrency());
        transaction1.setNote(transaction.getNote());
        transaction1.setCategory(transaction.getCategory());

        if(transaction.getDate() == null)
        {

            transaction1.setDate(DateUtils.createNow().getTime());
        }
        else {
            transaction1.setDate(transaction.getDate());
        }

        return transactionRepository.save(transaction1);

    }

    public Pageable getPageable(int page,int size, String sort){

        String[] sortParams = sort.split(",");
        String fieldName = sortParams[0];
        String direction = sortParams.length > 1 ? sortParams[1] : "asc";
        Sort sortOrder = Sort.by(Sort.Order.by(fieldName).with(
                Sort.Direction.fromString(direction)
        ));

        return PageRequest.of(page, size, sortOrder);

    }

    public Page<Transaction> getTransactionForBudget(Long budgetId, String search , String sort , int page, int size){


        return transactionRepository.findByBudgetIdAndNoteContainingIgnoreCase(budgetId,
                search,
                getPageable(page,size,sort));

    }

    public Page<Transaction> getTransactionForCategory(Long categoryId, String search , String sort , int page, int size){


        return transactionRepository.findByCategoryIdAndNoteContainingIgnoreCase(categoryId,
                search,
                getPageable(page,size,sort));

    }

    public Transaction findTransactionIdByNote(String note , Long budgetId){

        return transactionRepository.findByNoteContainingAndBudgetId(note,budgetId).orElse(null);
    }

    public void delete(Long transactionId, User user) throws AccessDeniedException {


        findOne(transactionId,user);

        transactionRepository.deleteById(transactionId);
    }

    public BigDecimal sumTotalForBudget(Long budgetId){


        return transactionRepository.sumTotalByBudgetId(budgetId);

    }

}
