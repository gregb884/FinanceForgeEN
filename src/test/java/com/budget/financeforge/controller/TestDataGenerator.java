package com.budget.financeforge.controller;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.enumpaket.Currency;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TestDataGenerator {

    public static Budget createBudget(String name, Currency currency) {
        Budget budget = new Budget();
        budget.setId(generateId());
        budget.setName(name);
        budget.setStartDate(new Date());
        budget.setEndDate(addDays(new Date(), 365)); // Adding 365 days for the end date
        budget.setTotal(new BigDecimal("10000"));
        budget.setSavings(new BigDecimal("1000"));
        budget.setCurrency(currency);

        budget.setUsers(createUsers(3));
        budget.setTransactions(createTransactions(3));
        budget.setGroups(createGroups(2));
        budget.setSubscriptions(createSubscriptions(3));
        budget.setCredits(createCredits(3));
        budget.setSalaries(createSalaries(3));

        return budget;
    }

    public static int idCounter = 1000; // Zakładając, że zaczynamy od 1000

    public static long generateId() {
        if (idCounter < 9999) {
            return idCounter++;
        } else {
            throw new RuntimeException("Osiągnięto limit ID");
        }
    }

    public static Set<User> createUsers(int count) {
        Set<User> users = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setId(generateId());
            user.setUsername("User" + i);
            users.add(user);
        }
        return users;
    }

    public static Set<Transaction> createTransactions(int count) {
        Set<Transaction> transactions = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            Transaction transaction = new Transaction();
            transaction.setId(generateId());
            transaction.setDate(new Date());
            transaction.setTotal(new BigDecimal("100"));
            transaction.setNote("Transaction" + i);
            transactions.add(transaction);
        }

        return transactions;
    }

    public static Set<Group> createGroups(int count) {
        Set<Group> groups = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            Group group = new Group();
            group.setId(generateId());
            if(i == 1)
            {
                group.setName("Ground Cost");
            }

            if(i == 2)
            {
                group.setName("Additional Cost");
            }
            groups.add(group);
        }
        return groups;
    }

    public static Set<Credit> createCredits(int count) {
        Set<Credit> credits = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            Credit credit = new Credit();
            credit.setId(generateId());
            credit.setName("Credit" + i);
            credit.setTotal(new BigDecimal("5000"));
            credits.add(credit);
        }
        return credits;
    }

    public static Set<Salary> createSalaries(int count) {
        Set<Salary> salaries = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            Salary salary = new Salary();
            salary.setId(generateId());
            salary.setTotal(new BigDecimal("3000"));
            salaries.add(salary);
        }
        return salaries;
    }

    public static Set<Subscription> createSubscriptions(int count) {
        Set<Subscription> subscriptions = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            Subscription subscription = new Subscription();
            subscription.setId(generateId());
            subscription.setName("Subscription" + i);
            subscription.setTotal(new BigDecimal("15"));
            subscriptions.add(subscription);
        }
        return subscriptions;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
