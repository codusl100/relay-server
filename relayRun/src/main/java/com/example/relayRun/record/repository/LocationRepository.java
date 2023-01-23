package com.example.relayRun.record.repository;

import com.example.relayRun.record.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository <LocationEntity, Long> {
    // mapping되어있는 RunningRecord 엔티티의 idx로 찾기
    List<LocationEntity> findByRecordIdx_RunningRecordIdx(Long idx);
}
