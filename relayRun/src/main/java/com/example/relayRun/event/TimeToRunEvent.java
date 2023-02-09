package com.example.relayRun.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class TimeToRunEvent {
    private Long memberStatusIdx;
    private LocalTime start;
}

