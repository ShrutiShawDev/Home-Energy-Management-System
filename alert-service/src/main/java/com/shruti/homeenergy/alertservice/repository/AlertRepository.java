package com.shruti.homeenergy.alertservice.repository;

import com.shruti.homeenergy.alertservice.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
}
