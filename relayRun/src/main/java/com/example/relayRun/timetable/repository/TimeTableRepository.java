package com.example.relayRun.timetable.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.timetable.entity.TimeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.Optional;

public interface TimeTableRepository extends JpaRepository<TimeTableEntity, Long> {
    Optional<TimeTableEntity> findByMemberStatusIdxAndDayAndStartLessThanEqualAndEndGreaterThanEqual(
            MemberStatusEntity memberStatusIdx,
            int day, LocalTime start, LocalTime end
    );
}
