package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    private Long id;
    private String name;
    private BigDecimal total;
    private boolean paid;
    private Budget budget;
    private Currency currency;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JsonIgnore
    public Budget getBudget() {
        return budget;
    }

}
