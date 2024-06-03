package com.budget.financeforge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

@Entity
public class Authority implements GrantedAuthority {


    @Serial
    private static final long serialVersionUID = -6200173144655494648L;
    private Long id;
    private String authority;
    private User user;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @JsonIgnore
    @ManyToOne
    public User getUsers() {
        return user;
    }

    public void setUsers(User user) {
        this.user = user;
    }
}
