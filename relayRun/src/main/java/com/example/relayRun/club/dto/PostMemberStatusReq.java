package com.example.relayRun.club.dto;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.GoalType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMemberStatusReq {
    private Long memberStatusIdx;
    private ClubEntity clubIdx;
    private UserProfileEntity userProfileIdx;
    private Integer day;
    private LocalDateTime start;
    private LocalDateTime end;
    private Float goal;
    private GoalType goalType;
}
