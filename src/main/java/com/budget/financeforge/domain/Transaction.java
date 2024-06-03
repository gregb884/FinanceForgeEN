package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Comparable<Transaction>{

    private Long id;
    private Date date;
    private BigDecimal total;
    private String note;
    private Date addDate;
    private Category category;
    private Budget budget;
    private Currency currency;


    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getDate() {
        return date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JsonProperty("category")
    public Category getCategory() {
        return category;
    }

    @ManyToOne
    @JsonProperty("budget")
    public Budget getBudget() {
        return budget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Column(nullable = false)
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public int compareTo(Transaction t) {

        return t.date.compareTo(this.date);
    }
}
