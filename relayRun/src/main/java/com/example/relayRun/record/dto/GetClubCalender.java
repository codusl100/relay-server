package com.example.relayRun.record.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetClubCalender {
//    @JsonFormat(pattern="yyyy-MM-dd")
//    LocalDate date;

    Double totalTime;

    Double totalDist;

    Double avgPace;

}
