package com.example.relayRun.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class BatonTouchEvent {
    private Long fromUserProfile;
    private int day;
    private LocalTime endTime;
}
