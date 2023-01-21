package com.example.relayRun.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostProfileReq {
    @ApiModelProperty(example = "닉네임")
    private String nickname;

    @ApiModelProperty(example = "상태메세지")
    private String statusMsg;

    @ApiModelProperty(example = "y")
    private String isAlarmOn;

    @ApiModelProperty(example= "이미지 경로")
    private String imgUrl;
}
