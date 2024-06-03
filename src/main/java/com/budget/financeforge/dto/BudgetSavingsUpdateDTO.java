package com.budget.financeforge.dto;

import java.math.BigDecimal;

public class BudgetSavingsUpdateDTO {
    private BigDecimal savings;

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(BigDecimal savings) {
        this.savings = savings;
    }
}