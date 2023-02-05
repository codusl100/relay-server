package com.example.relayRun.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Builder
public class GetClubCalender {
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate date;

    Double totalTime;

    Double totalDist;

    Double avgPace;

}
