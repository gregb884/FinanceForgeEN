package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {


    void deleteById(Long groupId);

}
