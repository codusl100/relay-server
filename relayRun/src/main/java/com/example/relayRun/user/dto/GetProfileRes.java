package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProfileRes {
    private Long userProfileIdx;
    private String nickname;
    private String statusMsg;
    private String isAlarmOn;
    private String imgUrl;
}
