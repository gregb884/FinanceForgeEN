package com.budget.financeforge.domain;

import com.budget.financeforge.enumpaket.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget implements Comparable<Budget> {

    private Long id;
    private Date startDate;
    private Date endDate;
    private String name;
    private BigDecimal total;
    private BigDecimal savings;
    private Set<User> users = new HashSet<>();
    private Set<Transaction> transactions = new TreeSet<>();
    private Set<Group> groups = new TreeSet<>();
    private Set<Subscription> subscriptions = new TreeSet<>();
    private Set<Credit> credits = new TreeSet<>();
    private Set<Salary> salaries = new TreeSet<>();
    private Currency currency;


    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getStartDate() {
        return startDate;
    }

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    public Date getEndDate() {
        return endDate;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "budget")
    @JsonIgnore
    public Set<Credit> getCredits() {
        return credits;
    }

    @ManyToMany
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "user_id"),joinColumns = @JoinColumn(name = "budget_id"))
    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "budget")
    @JsonIgnore
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "budget")
    @JsonIgnore
    public Set<Group> getGroups() {
        return groups;
    }

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "budget")
    @JsonIgnore
    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public int compareTo(Budget budget) {



        return budget.id.compareTo(this.id);

    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "budget")
    @JsonIgnore
    public Set<Salary> getSalaries() {
        return salaries;
    }
}
