package com.example.relayRun.record.repository;

import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;

public interface RecordRepository extends JpaRepository<RunningRecordEntity, Long> {
    Optional<RunningRecordEntity> findByRunningRecordIdxAndStatus(Long idx, String status);
    Optional<RunningRecordEntity> findByMemberStatusIdx_MemberStatusIdxAndCreatedAt(Long memberStatusIdx, LocalDateTime createdAt);
}
