package com.budget.financeforge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User  {


    private Long id;
    private String username;
    private String password;
    private String confirmPassword;
    private Set<Authority> authorities = new HashSet<>();
    private Set<Budget> budgets = new TreeSet<>();
    private int loggedCount;
    private Settings settings;
    private String activationCode;
    private boolean accountActive;



    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy = "users")
    public Set<Budget> getBudgets() {
        return budgets;
    }
    
    @Id
    @GeneratedValue(strategy  = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @Transient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "users")
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    public Settings getSettings() {
        return settings;
    }
}
