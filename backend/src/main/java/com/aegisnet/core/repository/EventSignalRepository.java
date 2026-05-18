package com.aegisnet.core.repository;

import com.aegisnet.core.model.EventSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventSignalRepository extends JpaRepository<EventSignal, Long> {
    List<EventSignal> findByProcessedFalse();
    List<EventSignal> findByTimestampAfter(LocalDateTime timestamp);
}
