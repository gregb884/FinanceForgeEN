package com.budget.financeforge.service;

import com.budget.financeforge.domain.Authority;
import com.budget.financeforge.domain.Settings;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.UserDto;
import com.budget.financeforge.repository.SettingsRepository;
import com.budget.financeforge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {


    private final UserRepository repository;

    private final SettingsRepository settingsRepository;

    private final ActivationService activationService;



    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, SettingsRepository settingsRepository, PasswordEncoder passwordEncoder, ActivationService activationService) {
        this.repository = repository;
        this.settingsRepository = settingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.activationService = activationService;
    }

    public void saveUser(UserDto user)
    {


        User user1 = new User();

        user1.setUsername(user.getUsername());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));

        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUsers(user1);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        user1.setAuthorities(authorities);

        Settings settings = new Settings();

        settings.setUser(user1);
        settings.setIsAdditionalViewHidden(false);
        settings.setIsCreditViewHidden(false);
        settings.setIsSubscriptionViewHidden(false);


        user1.setSettings(settings);

        user1.setAccountActive(true);

        user1.setActivationCode(activationService.generateActivationToken());

        repository.save(user1);

    }


    public boolean confirmUser(String confirmationCode){

        User user = repository.findByActivationCode(confirmationCode);

        user.setAccountActive(true);
        repository.save(user);

        return true;
    }


    public Boolean checkUser(UserDto user) {

        User userNameCheck = repository.findByUsername(user.getUsername());

        return userNameCheck == null || !userNameCheck.getUsername().equals(user.getUsername());

    }


    public User findUserByUsername(String username) {
        return repository.findByUsername(username);
    }


    public boolean resetLink(String email) {

        User user = findUserByUsername(email);

        if (user == null) return false;


        return true;

    }

    public Boolean changePassword(String activationCode, String password) {

        User user = repository.findByActivationCode(activationCode);

        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);

        return true;
    }
}
