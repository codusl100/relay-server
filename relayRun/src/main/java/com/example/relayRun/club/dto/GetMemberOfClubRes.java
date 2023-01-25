package com.example.relayRun.club.dto;

import com.example.relayRun.record.dto.GetRecordWithoutLocationRes;
import com.example.relayRun.user.dto.GetProfileRes;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMemberOfClubRes {

    @ApiModelProperty(example = "지원 상태 idx")
    private Long memberStatusIdx;

    @ApiModelProperty(example = "프로필 정보")
    private GetProfileRes userProfile;

    @ApiModelProperty(example = "해당 프로필 시간표")
    private GetTimeTableListRes timeTable;

    @ApiModelProperty(example = "당일 달리기 기록")
    private List<GetRecordWithoutLocationRes> runningRecord;
}
