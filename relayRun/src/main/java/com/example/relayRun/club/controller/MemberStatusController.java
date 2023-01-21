package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.timetable.service.TimeTableService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/member-status")
public class MemberStatusController {

    private final MemberStatusService memberStatusService;
    private TimeTableService timeTableService;

    public MemberStatusController(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    @ResponseBody
    @PostMapping("")   //principal 적용
    public BaseResponse<String> createMemberStatus(Principal principal, @RequestBody PostMemberStatusReq memberStatus) {
        try {
            memberStatusService.createMemberStatus(principal, memberStatus);
            timeTableService.createTimeTable(memberStatus);
            return new BaseResponse<>("그룹 신청 완료");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }
}
