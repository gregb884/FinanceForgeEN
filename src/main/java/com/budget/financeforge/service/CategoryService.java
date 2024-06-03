package com.budget.financeforge.service;

import com.budget.financeforge.domain.*;
import com.budget.financeforge.dto.CategoryDto;
import com.budget.financeforge.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@Service
public class CategoryService{



   private final CategoryRepository repository;


   private final GroupService groupService;


   private final BudgetService budgetService;

    public CategoryService(CategoryRepository repository, GroupService groupService, BudgetService budgetService) {
        this.repository = repository;
        this.groupService = groupService;
        this.budgetService = budgetService;
    }

    public Category save(CategoryDto categoryDto , Long groupId , User user) throws AccessDeniedException {


        Group group = groupService.findOne(groupId, user);

        Budget budget = budgetService.findOne(group.getBudget().getId(), user);



        if(categoryDto.getPlanned()== null)
        {
            categoryDto.setPlanned(BigDecimal.ZERO);
        }


        Category category1 = new Category();

        category1.setName(categoryDto.getName());
        category1.setPlanned(categoryDto.getPlanned());
        category1.setGroup(group);
        group.getCategories().add(category1);
        category1.setCurrency(budget.getCurrency());
        category1.setSpent(BigDecimal.valueOf(0));


        return repository.save(category1);
    }

    public void updateCreditCategory(Category category){

        Category category1 = repository.findById(category.getId()).orElseThrow(() -> new RuntimeException("Category not found"));

        category1.setPlanned(category.getPlanned());

        repository.save(category1);

    }

    public Category findCategoryIdByName(String name, User user, Long budgetId) throws AccessDeniedException {


       Budget budget = budgetService.findOne(budgetId,user);

       Category category = budget.getGroups().stream()
               .flatMap(group -> group.getCategories().stream())
               .filter(category1 -> name.equals(category1.getName())).findFirst().orElseThrow(() -> new RuntimeException("Category not found"));



        if (category.getGroup().getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }

        return category;

    }

    public Category findOne(Long categoryId, User user) throws AccessDeniedException {

        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));


        if (category.getGroup().getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }


        return category;

    }

    public void updateCategory(CategoryDto categoryDto , Long categoryId, User user) throws AccessDeniedException {

        Category category1 = findOne(categoryId, user);

        category1.setName(categoryDto.getName());
        category1.setCurrency(category1.getGroup().getBudget().getCurrency());
        category1.setPlanned(categoryDto.getPlanned());


        repository.save(category1);
    }


    public Page<Category> allCategoryForGroup(Long budgetId, String search , String sort , int page, int size){

        return repository.findByGroupId(budgetId,
                getPageable(page,size,sort));

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

    public void delete(Long categoryId) {



        repository.deleteById(categoryId);
    }
}
