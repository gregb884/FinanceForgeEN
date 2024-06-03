package com.budget.financeforge.service;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.BudgetDto;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class BudgetService {


    private final BudgetRepository budgetRepository;

    private final GroupService groupService;

    private final CategoryRepository categoryRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final CreditRepository creditRepository;




    public BudgetService(BudgetRepository budgetRepository, GroupService groupService, CategoryRepository categoryRepository, SubscriptionRepository subscriptionRepository, CreditRepository creditRepository) {
        this.groupService = groupService;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.creditRepository = creditRepository;
    }


    public TreeSet<Budget> getBudgets(@AuthenticationPrincipal User user) {

        Set<User> users = new HashSet<>();
        users.add(user);

        return budgetRepository.findByUsersIn(users);


    }

    public Budget saveBudget(User user, BudgetDto budgetDto) {

        Budget createdBudget = new Budget();

        createdBudget.setName(budgetDto.getName());
        createdBudget.setStartDate(budgetDto.getStartDate());
        createdBudget.setEndDate(budgetDto.getEndDate());
        createdBudget.setCurrency(budgetDto.getCurrency());

        Set<User> users = new HashSet<>();
        Set<Budget> budgets = new HashSet<>();
        Group group = new Group();
        group.setName("Ground Cost");
        group.setBudget(createdBudget);
        users.add(user);
        budgets.add(createdBudget);
        createdBudget.setUsers(users);
        createdBudget.getGroups().add(group);
        createdBudget.setTotal(BigDecimal.ZERO);
        createdBudget.setSavings(BigDecimal.ZERO);

        LocalDate startDate = budgetDto.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = budgetDto.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Locale locale = new Locale("pl", "PL");

        String startMonthName = startDate.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, locale);
        String endMonthName = endDate.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, locale);

        if (startMonthName.equals(endMonthName)) {
            createdBudget.setName(startMonthName + " " + startDate.getYear());
        } else {

            createdBudget.setName(startMonthName + " - " + endMonthName + " " + startDate.getYear());
        }


        budgetRepository.save(createdBudget);

        return createdBudget;
    }

    public void updateSavings(Budget budget){


        budgetRepository.save(budget);

    }


    public Budget findOne(Long budgetId, User user) throws AccessDeniedException {


        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));


        if (budget.getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }

        return budget;

    }

    public Page<Budget> findBudgetPaginated(User user,Pageable pageable){

        return budgetRepository.findBudgetByUsers(user,pageable);
    }

    public Page<Budget> findBudgetPaginatedWithoutLastMonth(User user,Pageable pageable){


        Page<Budget> budgetPage = budgetRepository.findBudgetByUsers(user,pageable);

        List<Budget> budgets = new ArrayList<>(budgetPage.getContent());

        if (!budgets.isEmpty()) {

            budgets.remove(0);
        }

        return new PageImpl<>(budgets, pageable, budgetPage.getTotalElements());

    }


    public BigDecimal totalPlanned(Long budgetId, User user) throws AccessDeniedException {

        Budget budget = findOne(budgetId, user);

        budget.setGroups(new TreeSet<>(budget.getGroups()));

        if (budget != null) {

            Set<Category> categories = new TreeSet<>(budget.getGroups().stream().findFirst().get().getCategories());

            return categories.stream()
                    .map(Category::getPlanned)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {

            return BigDecimal.ZERO;
        }
    }

   public void addSalary(Long budgetId, BigDecimal total){

        Budget budget = budgetRepository.findById(budgetId).orElse(null);

        budget.setTotal(budget.getTotal().add(total));

        budgetRepository.save(budget);



   }


    public void deleteSalaryFromBalance(Long budgetId, BigDecimal total){

        Budget budget = budgetRepository.findById(budgetId).orElse(null);

        budget.setTotal(budget.getTotal().subtract(total));

        budgetRepository.save(budget);

    }


    public void transferDataFromAnotherBudget(Long newBudgetId, Long oldBudgetId, User user) throws AccessDeniedException {


        Budget budgetNew = findOne(newBudgetId,user);

        Group groupNew = budgetNew.getGroups().stream().findFirst().orElse(null);

        Budget budgetOld = findOne(oldBudgetId,user);

        budgetNew.setCurrency(budgetOld.getCurrency());
        budgetNew.setSavings(budgetOld.getSavings());



        Group groupOld = budgetOld.getGroups().stream().filter(group -> group.getName().equals("Ground Cost")).findFirst().orElse(null);

        assert groupOld != null;

        Set<Category> categoriesOld = groupOld.getCategories();

        Iterator<Category> iterator = categoriesOld.iterator();

        Set<Subscription> subscriptionsOld = budgetOld.getSubscriptions();

        Iterator<Subscription> subscriptionIterator = subscriptionsOld.iterator();

        while (iterator.hasNext()) {

            Category category = iterator.next();

            CategoryDto categoryDto = new CategoryDto(category.getPlanned(), category.getName());


            saveCategoryInBudgetService(categoryDto,groupNew.getId(),user);

        }


        while (subscriptionIterator.hasNext()){

            Subscription subscription = subscriptionIterator.next();

            Subscription subscription1 = new Subscription();

            subscription1.setName(subscription.getName());
            subscription1.setCurrency(subscription.getCurrency());
            subscription1.setTotal(subscription.getTotal());
            subscription1.setPaid(false);
            subscription1.setBudget(budgetNew);

            subscriptionRepository.save(subscription1);

        }


        Set<Credit> creditsOld = budgetOld.getCredits();

        Iterator<Credit> creditIterator = creditsOld.iterator();

        while (creditIterator.hasNext())
        {

            Credit credit = creditIterator.next();

            Credit credit1 = new Credit();

            credit1.setBudget(budgetNew);
            credit1.setRepaid(credit.getRepaid());
            credit1.setPayment(credit.getPayment());
            credit1.setName(credit.getName());
            credit1.setCurrency(credit.getCurrency());
            credit1.setActualPart(credit.getActualPart());
            credit1.setTotalParts(credit.getTotalParts());
            credit1.setTotal(credit.getTotal());
            credit1.setStartDate(credit.getStartDate());
            credit1.setEndDate(credit.getEndDate());

            creditRepository.save(credit1);

        }

        budgetRepository.save(budgetNew);

    }



    private void saveCategoryInBudgetService(CategoryDto categoryDto, Long groupId, User user) throws AccessDeniedException {
        Group group = groupService.findOne(groupId, user);
        Budget budget = findOne(group.getBudget().getId(), user);
        Category category1 = new Category();
        category1.setName(categoryDto.getName());
        category1.setPlanned(categoryDto.getPlanned());
        category1.setGroup(group);
        group.getCategories().add(category1);
        category1.setCurrency(budget.getCurrency());
        category1.setSpent(BigDecimal.valueOf(0));
        categoryRepository.save(category1);
    }


    public Boolean chartSpendView(Budget budget)
    {

       int uniqueCategories =  budgetRepository.countUniqueCategoriesByBudgetId(budget.getId());

        return uniqueCategories >= 3;
    }



    public String delete(Long budgetId, User user) throws AccessDeniedException {


        Budget budget = findOne(budgetId,user);



        budgetRepository.deleteById(budgetId);


        return "redirect:/panel";

    }


    public BigDecimal restToSpendMoney(Long budgetId, User user) throws AccessDeniedException {


        Budget budget = findOne(budgetId,user);

        return budget.getGroups().stream()
                .map(group -> group.getCategories().stream()
                        .map(Category::getRemaing)
                        .filter(remaining -> remaining.compareTo(BigDecimal.ZERO) >= 0)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

}
