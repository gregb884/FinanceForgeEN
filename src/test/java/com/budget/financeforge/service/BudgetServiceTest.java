package com.budget.financeforge.service;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

import com.budget.financeforge.controller.BudgetController;
import com.budget.financeforge.controller.TestDataGenerator;
import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Category;
import com.budget.financeforge.domain.Group;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.BudgetDto;
import com.budget.financeforge.dto.UserDto;
import com.budget.financeforge.enumpaket.Currency;
import com.budget.financeforge.repository.BudgetRepository;
import com.budget.financeforge.repository.CategoryRepository;
import com.budget.financeforge.repository.CreditRepository;
import com.budget.financeforge.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CreditRepository creditRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Mock
    private GroupService groupService;

    @Mock
    private Authentication principal;

    @InjectMocks
    private BudgetController budgetController;


    @BeforeEach
    void setUp() {




    }


    @Test
    void shouldSaveBudgetSuccessfully() throws ParseException {
        // Given
        User user = new User();
        BudgetDto budgetDto = new BudgetDto();

        budgetDto.setName("Test Name Budget");
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date dateStart = simpleDateFormat.parse("2024-02-01");
        Date dateEnd = simpleDateFormat.parse("2024-02-01");
        budgetDto.setStartDate(dateStart);
        budgetDto.setEndDate(dateEnd);
        budgetDto.setCurrency(Currency.valueOf("EUR"));

        Budget expectedBudget = new Budget();
        expectedBudget.setName("February 2024");
        expectedBudget.setCurrency(Currency.valueOf("EUR"));

        when(budgetRepository.save(any(Budget.class))).thenReturn(expectedBudget);

        // When
        Budget result = budgetService.saveBudget(user, budgetDto);

        // Then
        assertThat(result.getName()).isEqualTo("February 2024");
        assertThat(result.getCurrency()).isEqualTo(budgetDto.getCurrency());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void getBudgets_ShouldReturnUserBudgets() {
        // given
        User user = new User(); // załóżmy, że jest poprawnie zainicjalizowany
        Set<User> users = Set.of(user);
        TreeSet<Budget> expectedBudgets = new TreeSet<>(); // załóżmy, że jest poprawnie zainicjalizowany
        when(budgetRepository.findByUsersIn(users)).thenReturn(expectedBudgets);

        // when
        TreeSet<Budget> actualBudgets = budgetService.getBudgets(user);

        // then
        assertThat(actualBudgets).isEqualTo(expectedBudgets);
        verify(budgetRepository).findByUsersIn(users);
    }


    @Test
    void updateSavings_ShouldCallSaveOnRepository() {
        // given
       Budget budget = TestDataGenerator.createBudget("Test Budget 1", Currency.PLN);

        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        // when
        budgetService.updateSavings(budget);

        // then
        verify(budgetRepository).save(budget);
    }


    @Test
    void findOne_ShouldReturnBudget_WhenUserHasAccess() throws AccessDeniedException {

        // given
        Set<User> userSetWithAcces = TestDataGenerator.createUsers(1);

        User user1 = userSetWithAcces.iterator().next();

        Budget budget = TestDataGenerator.createBudget("Test Budget 1", Currency.PLN);

        budget.setUsers(userSetWithAcces);

        when(budgetRepository.findById(budget.getId())).thenReturn(Optional.of(budget));

        // when
        Budget actualBudget = budgetService.findOne(budget.getId(), user1 );

        // then

        assertThat(actualBudget).isEqualTo(budget);
    }

    @Test
    void findOne_ShouldThrowException_WhenUserHasNoAccess() {
        // given
        Set<User> userSetWithAcces = TestDataGenerator.createUsers(1);
        Set<User> userSetWithoutAcces = TestDataGenerator.createUsers(1);
        User userWithoutAcces = userSetWithoutAcces.iterator().next();

        Budget budget = TestDataGenerator.createBudget("Test Budget 1", Currency.PLN);

        budget.setUsers(userSetWithAcces);

        when(budgetRepository.findById(budget.getId())).thenReturn(Optional.of(budget));

        // then
        assertThrows(AccessDeniedException.class, () -> {
            // when
            budgetService.findOne(budget.getId(), userWithoutAcces);
        });
    }


    @Test
    void findBudgetPaginated_ShouldReturnPageOfBudgets() {
        // given
        User user = new User(); // poprawnie zainicjalizowany
        Pageable pageable = PageRequest.of(0, 10);
        Page<Budget> expectedPage = new PageImpl<>(List.of(new Budget()));


        when(budgetRepository.findBudgetByUsers(user, pageable)).thenReturn(expectedPage);

        // when
        Page<Budget> actualPage = budgetService.findBudgetPaginated(user, pageable);

        // then
        assertThat(actualPage).isEqualTo(expectedPage);
    }


    @Test
    void addSalary_ShouldAddTotalToBudget() {
        // given
        Long budgetId = 1L;
        BigDecimal salaryToAdd = BigDecimal.valueOf(1000);
        Budget budget = new Budget();
        budget.setTotal(BigDecimal.ZERO);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // when
        budgetService.addSalary(budgetId, salaryToAdd);

        // then
        assertThat(budget.getTotal()).isEqualByComparingTo("1000");
        verify(budgetRepository).save(budget);
    }


    @Test
    void deleteSalaryFromBalance_ShouldSubtractTotalFromBudget() {
        // given
        Long budgetId = 1L;
        BigDecimal salaryToRemove = BigDecimal.valueOf(500);
        Budget budget = new Budget();
        budget.setTotal(BigDecimal.valueOf(1500));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // when
        budgetService.deleteSalaryFromBalance(budgetId, salaryToRemove);

        // then
        assertThat(budget.getTotal()).isEqualByComparingTo("1000");
        verify(budgetRepository).save(budget);
    }


//    @Test
//    void transferDataFromAnotherBudget_ShouldTransferDataCorrectly() throws AccessDeniedException, ParseException {
//        // given
//
//        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
//
//        Set<User> userList = TestDataGenerator.createUsers(1);
//
//        Budget budgetNew = new Budget();
//        budgetNew.setId(45L);
//        String pattern = "yyyy-MM-dd";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        Date dateStart = simpleDateFormat.parse("2024-02-01");
//        Date dateEnd = simpleDateFormat.parse("2024-02-28");
//        budgetNew.setStartDate(dateStart);
//        budgetNew.setEndDate(dateEnd);
//        Set<Group> groups = new TreeSet<>();
//        Group group1 = new Group();
//        group1.setId(1L);
//        group1.setName("Ground Cost");
//        group1.setBudget(budgetNew);
//        groups.add(group1);
//        Group group2 = new Group();
//        group2.setId(2L);
//        group2.setName("Additional Cost");
//        group2.setBudget(budgetNew);
//        budgetNew.setGroups(groups);
//
//
//        Budget budgetOld = TestDataGenerator.createBudget("Test Budget 1", Currency.PLN);
//
//        budgetNew.setUsers(budgetOld.getUsers());
//        budgetNew.setName("Test Budget 2");
//        budgetNew.setCurrency(Currency.PLN);
//
//        budgetOld.setCurrency(Currency.valueOf("USD"));
//        budgetOld.setSavings(BigDecimal.valueOf(100));
//        budgetOld.setId(2L);
//
//        budgetNew.setUsers(userList);
//        budgetOld.setUsers(userList);
//
//        Set<Category> categories = new HashSet<>();
//
//        Category category1 = new Category();
//        category1.setId(1L);
//        category1.setName("Test Category 1");
//        categories.add(category1);
//
//       budgetOld.getGroups().stream().filter(group -> group.getName().equals("Ground Cost")).findFirst().get().setCategories(categories);
//
//        User user = userList.iterator().next();
//
//        Set<Budget> budgetSet = new HashSet<>();
//
//        budgetSet.add(budgetNew);
//        budgetSet.add(budgetOld);
//
//        user.setBudgets(budgetSet);
//
//
//        List<String> listCategoryOld = budgetOld.getGroups().stream().filter(group -> group.getName().equals("Ground Cost")).findFirst().get()
//                        .getCategories().stream().map(Category::getName).toList();
//
//        Category category2 = new Category();
//        category2.setId(2L);
//        category2.setName("Test Category 1");
//
//
//         //when
//
//        when(budgetRepository.findById(2L)).thenReturn(Optional.of(budgetOld));
//        when(budgetRepository.findById(45L)).thenReturn(Optional.of(budgetNew));
//        when(groupService.findOne(1L,user)).thenReturn(group1);
//        when(groupService.findOne(2L,user)).thenReturn(group2);
//
//
//
//        budgetService.transferDataFromAnotherBudget(budgetNew.getId(), budgetOld.getId(), user);
//
//
//
//        verify(budgetRepository).save(captor.capture());
//
//
//        Budget savedBudget = captor.getValue();
//


         //then

//        List<String> listCategoryNew = savedBudget.getGroups().stream().filter(group -> group.getName().equals("Ground Cost")).findFirst().get()
//                .getCategories().stream().map(Category::getName).toList();
//
//
//        assertThat(budgetNew.getSavings()).isEqualByComparingTo("100");
//        assertThat(listCategoryNew).containsExactlyInAnyOrderElementsOf(listCategoryOld);

    //}

}
