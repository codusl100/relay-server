package com.example.relayRun.record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetLocationRes {
    private LocalDateTime time;
    private Integer position;
    private String status;
}
