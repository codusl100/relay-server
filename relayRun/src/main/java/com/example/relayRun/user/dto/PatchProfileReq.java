package com.example.relayRun.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchProfileReq {
    private String nickName;
    private String imgUrl;
    private String statusMsg;
}
