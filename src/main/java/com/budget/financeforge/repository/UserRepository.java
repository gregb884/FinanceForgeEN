package com.budget.financeforge.repository;

import com.budget.financeforge.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


    User findByUsername(String username);

    User findByActivationCode(String activationCode);

}
