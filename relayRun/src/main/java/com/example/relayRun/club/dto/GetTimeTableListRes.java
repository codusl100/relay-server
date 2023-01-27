package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class GetTimeTableListRes {
    private Long timeTableIdx;
    private Integer day;
    private LocalDateTime start;
    private LocalDateTime end;
    private Float goal;
    private GoalType goalType;
}
