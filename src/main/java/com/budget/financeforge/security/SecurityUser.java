package com.budget.financeforge.security;

import com.budget.financeforge.domain.Authority;
import com.budget.financeforge.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Set;

public class SecurityUser extends User implements UserDetails {

    @Serial
    private static final long serialVersionUID = -4642823973715602256L;

    public SecurityUser(){}
    public SecurityUser(User user) {
        this.setAuthorities(user.getAuthorities());
        this.setId(user.getId());
        this.setPassword(user.getPassword());
        this.setUsername(user.getUsername());
        this.setAccountActive(user.isAccountActive());
    }

    @Override
    public Set<Authority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
