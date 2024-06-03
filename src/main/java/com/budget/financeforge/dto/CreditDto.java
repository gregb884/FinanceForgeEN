package com.budget.financeforge.dto;

import com.budget.financeforge.enumpaket.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {

    private String name;
    private BigDecimal total;
    private BigDecimal payment;
    private int actualPart;
    private int totalParts;
    private Date startDate;
    private Date endDate;
    private Currency currency;
    private BigDecimal repaid;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getStartDate() {
        return startDate;
    }

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getEndDate() {
        return endDate;
    }
}
