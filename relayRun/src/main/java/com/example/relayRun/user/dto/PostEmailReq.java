package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class PostEmailReq {
    @Email
    @ApiModelProperty(example = "인증 번호 보낼 이메일")
    private String email;
}
