package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetTimeTableListRes;
import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/clubs/member-status")
public class MemberStatusController {

    private final MemberStatusService memberStatusService;

    public MemberStatusController(MemberStatusService memberStatusService) {
        this.memberStatusService = memberStatusService;
    }

    @ResponseBody
    @PostMapping("/{clubIdx}")
    public BaseResponse<String> createMemberStatus(@PathVariable Long clubIdx, @Valid @RequestBody PostMemberStatusReq memberStatus) {
        try {
            memberStatusService.createMemberStatus(clubIdx, memberStatus);
            return new BaseResponse<>("그룹 신청 완료");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{clubIdx}")
    public BaseResponse<List<GetTimeTableListRes>> getTimeTables(@PathVariable Long clubIdx) {
        try {
            List<GetTimeTableListRes> timeTableList = memberStatusService.getTimeTables(clubIdx);
            return new BaseResponse<>(timeTableList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
