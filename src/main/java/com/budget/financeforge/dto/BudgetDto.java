package com.budget.financeforge.dto;

import com.budget.financeforge.enumpaket.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {


    private String name;
    private Date startDate;
    private Date endDate;
    private Currency currency;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getStartDate() {
        return startDate;
    }

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getEndDate() {
        return endDate;
    }
}
