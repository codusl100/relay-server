package com.example.relayRun.record.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetClubCalender {
//    @JsonFormat(pattern="yyyy-MM-dd")
//    LocalDate date;

    float totalTime;

    float totalDist;

    float avgPace;

}
