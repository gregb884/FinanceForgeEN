package com.budget.financeforge.dto;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.enumpaket.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDto {


    private String note;
    private BigDecimal total;
    private Budget budget;
    private Currency currency;


}
