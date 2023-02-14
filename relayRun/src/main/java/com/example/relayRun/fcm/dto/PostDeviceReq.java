package com.example.relayRun.fcm.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDeviceReq {
    private String email;
    private String userDeviceID;
}
