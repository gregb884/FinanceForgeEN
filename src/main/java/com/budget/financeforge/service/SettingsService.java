package com.budget.financeforge.service;

import com.budget.financeforge.domain.Settings;
import com.budget.financeforge.domain.User;
import com.budget.financeforge.dto.SettingsDto;
import com.budget.financeforge.repository.SettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {


    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository){

        this.settingsRepository = settingsRepository;
    }




    public void updateSettings(User user, SettingsDto settingsDto) {

        Settings settings = settingsRepository.findByUserId(user.getId());

        if (settingsDto.getIsSubscriptionViewHidden() != null) {
            settings.setIsSubscriptionViewHidden(settingsDto.getIsSubscriptionViewHidden());
        }
        if (settingsDto.getIsAdditionalViewHidden() != null) {
            settings.setIsAdditionalViewHidden(settingsDto.getIsAdditionalViewHidden());
        }
        if (settingsDto.getIsCreditViewHidden() != null) {
            settings.setIsCreditViewHidden(settingsDto.getIsCreditViewHidden());
        }


        settingsRepository.save(settings);
    }

    public Settings findOne(Long id) {



        return settingsRepository.findByUserId(id);
    }
}
