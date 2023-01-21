package com.example.relayRun.club.dto;

import com.example.relayRun.util.GoalType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTableDTO {
    private Integer day;
    private String start;
    private String end;
    private Float goal;
    private GoalType goalType;
}
