package com.example.relayRun.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProfileReq {
    private String nickname;

    private String statusMsg;

    private String isAlarmOn;

    private String imgUrl;
}
