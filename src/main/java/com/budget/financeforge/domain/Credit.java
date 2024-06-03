package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credit implements Comparable<Credit> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private BigDecimal total;
    private BigDecimal payment;
    private int actualPart;
    private int totalParts;
    private Date startDate;
    private Date endDate;
    private Currency currency;
    private BigDecimal repaid;
    private BigDecimal moneyToCloseCredit;

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getStartDate() {
        return startDate;
    }

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getEndDate() {
        return endDate;
    }

    @ManyToOne
    private Budget budget;


    @Transient
    public BigDecimal getMoneyToCloseCredit() {
        int parts = totalParts - actualPart;

        return this.payment.multiply(BigDecimal.valueOf(parts));
    }


    @Override
    public int compareTo(Credit o) {

       return this.id.compareTo(o.id);
    }
}
