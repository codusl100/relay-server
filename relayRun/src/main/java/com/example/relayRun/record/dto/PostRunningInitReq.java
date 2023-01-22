package com.example.relayRun.record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRunningInitReq {
    private Long profileIdx;
    private Long groupIdx;

}
