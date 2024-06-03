package com.budget.financeforge.restController;

import com.budget.financeforge.domain.Category;
import com.budget.financeforge.domain.Transaction;
import com.budget.financeforge.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController {


   private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/data")
            public ResponseEntity<Page<Category>> getCategoryForGroup(
                    @RequestParam Long groupId,
                    @RequestParam(required = false) String search,
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size,
                    @RequestParam(defaultValue = "planned,desc") String sort){


                Page<Category> pageResult = categoryService.allCategoryForGroup(groupId,search,sort,page,size);


                return ResponseEntity.ok(pageResult);
            }


}
