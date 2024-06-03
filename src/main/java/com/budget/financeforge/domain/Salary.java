package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Salary implements Comparable<Salary> {


    private Long id;
    private String note;
    private BigDecimal total;
    private Budget budget;
    private Currency currency;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @ManyToOne
    public Budget getBudget() {
        return budget;
    }


    @Override
    public int compareTo(Salary s) {

        return Long.compare(this.id,s.id);
    }
}
