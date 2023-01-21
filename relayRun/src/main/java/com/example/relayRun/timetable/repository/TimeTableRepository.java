package com.example.relayRun.timetable.repository;

import com.example.relayRun.timetable.entity.TimeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTableEntity, Long> {

}
