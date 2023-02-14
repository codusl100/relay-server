package com.example.relayRun.club.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel(value = "멤버 변경 모델")
public class PatchDeleteMemberReq {
    @ApiModelProperty(example = "나갈/강퇴할 유저 프로필 idx", required = true)
    private Long userProfileIdx;
}
