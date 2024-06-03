package com.budget.financeforge.service;

import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ActivationService {


    public String generateActivationToken() {
        return UUID.randomUUID().toString();
    }

}
