package com.example.relayRun.record.repository;

import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<RunningRecordEntity, Long> {
    Optional<RunningRecordEntity> findByRunningRecordIdxAndStatus(Long idx, String status);
    @Query("select r from RunningRecordEntity r where r.memberStatusIdx = :memberStatusIdx and r.createdAt between :startDate and :endDate")
    List<RunningRecordEntity> selectByMemberStatusIdxAndDate(@Param("memberStatusIdx") Long memberStatusIdx,@Param("startDate") String startDate,@Param("endDate") String endDate);
}
