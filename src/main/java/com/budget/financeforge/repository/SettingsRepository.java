package com.budget.financeforge.repository;

import com.budget.financeforge.domain.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings,Long> {

        Settings findByUserId(Long userId);


}
