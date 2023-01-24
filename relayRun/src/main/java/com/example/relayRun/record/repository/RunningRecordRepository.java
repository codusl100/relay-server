package com.example.relayRun.record.repository;

import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {
}
