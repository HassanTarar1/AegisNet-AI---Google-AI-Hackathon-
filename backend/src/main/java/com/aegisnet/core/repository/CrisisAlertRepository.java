package com.aegisnet.core.repository;

import com.aegisnet.core.model.CrisisAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrisisAlertRepository extends JpaRepository<CrisisAlert, Long> {
    List<CrisisAlert> findByStatus(String status);
}
