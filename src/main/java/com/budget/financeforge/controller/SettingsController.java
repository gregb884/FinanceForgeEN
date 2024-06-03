package com.budget.financeforge.controller;


import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.SettingsDto;
import com.budget.financeforge.service.SettingsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budget/{budgetId}/settings")
public class SettingsController {


    private final SettingsService settingsService;


    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


    @PostMapping
    public String viewUpdate(@AuthenticationPrincipal User user,
                             @ModelAttribute SettingsDto settingsDto,
                             @PathVariable Long budgetId){


        settingsService.updateSettings(user,settingsDto);


        return "redirect:/budget/" + budgetId;
    }



}
