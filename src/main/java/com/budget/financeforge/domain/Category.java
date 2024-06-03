package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.io.BigDecimalParser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Comparable<Category> {

        private Long id;
        private BigDecimal planned;
        private String name;
        private Group group;
        private Set<Transaction> transactions = new TreeSet<>();
        private Currency currency;
        private BigDecimal spent;
        private BigDecimal remaing;


    @Transient
    public BigDecimal getSpent() {

        return BigDecimal.valueOf(this.getTransactions().stream().mapToDouble(transaction -> transaction.getTotal().doubleValue()).sum());

    }

    @Transient
    public BigDecimal getRemaing() {

        BigDecimal spent = this.getSpent() ;

        BigDecimal budget = this.getPlanned();

        return budget.subtract(spent);
    }

    @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JsonIgnore
    public Group getGroup() {
        return group;
    }

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy = "category")
    public Set<Transaction> getTransactions() {
        return transactions;
    }


    @Override
    public int compareTo(Category c) {

        return this.id.compareTo(c.id);
    }
}
