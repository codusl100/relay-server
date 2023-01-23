package com.example.relayRun.record.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class locationDTO {
    private String time;
    private Float longitude;
    private Float latitude;
    private String status;
}
