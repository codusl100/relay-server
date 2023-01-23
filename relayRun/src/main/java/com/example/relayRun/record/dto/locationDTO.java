package com.example.relayRun.record.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class locationDTO {
    private LocalDateTime time;
    private Float longitude;
    private Float latitude;
}
