package com.example.relayRun.record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRunningFinishReq {
    private Long runningRecordIdx;

    private Float distance;

    private Float pace;

    private String time;

    private List<locationDTO> locations;
}
