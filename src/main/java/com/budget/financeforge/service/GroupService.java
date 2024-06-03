package com.budget.financeforge.service;

import com.budget.financeforge.domain.Budget;
import com.budget.financeforge.domain.Group;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class GroupService {


    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }


    public Group findOne(Long groupId, User user) throws AccessDeniedException {


        Group group = repository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));


        if (group.getBudget().getUsers().stream().map(User::getId).noneMatch(id -> id.equals(user.getId()))){

            throw new AccessDeniedException("You do not have permission to view this budget");

        }

        return group;

    }


    public Group save(Group group) {


        return repository.save(group);

    }
}
