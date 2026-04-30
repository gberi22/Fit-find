package com.fitfind.fitfind.systemconfig.repository;

import com.fitfind.fitfind.systemconfig.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {

    SystemConfiguration findFirstByOrderByCreatedAtDesc();
}
