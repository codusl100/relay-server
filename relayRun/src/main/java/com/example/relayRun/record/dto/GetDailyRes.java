package com.example.relayRun.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetDailyRes {
    LocalDate date;
    float totalTime;
    float totalDist;
    float avgPace;
}
