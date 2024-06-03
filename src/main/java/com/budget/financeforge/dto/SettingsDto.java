package com.budget.financeforge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettingsDto {


    private Boolean isSubscriptionViewHidden;
    private Boolean isAdditionalViewHidden;
    private Boolean isCreditViewHidden;

}
