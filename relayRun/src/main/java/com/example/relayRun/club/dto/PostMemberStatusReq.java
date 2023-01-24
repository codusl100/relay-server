package com.example.relayRun.club.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMemberStatusReq {
    private Long userProfileIdx;
    private List<TimeTableDTO> timeTables;
}
