package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.PatchClubInfoReq;
import com.example.relayRun.club.dto.PatchHostReq;
import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Api(tags = {"그룹 생성/관리 관련 API"})
@RequestMapping(value = "/clubs/apply")
public class ClubApplyController {

    private final ClubService clubService;

    public ClubApplyController(ClubService clubService) {
        this.clubService = clubService;
    }


    // 그룹 생성
    @ResponseBody
    @PostMapping("/{userProfileIdx}/generates")
    @ApiResponses({
            @ApiResponse(code = 200, message = "그룹 생성 완료"),
            @ApiResponse(code = 401, message = "권한을 찾을 수 없습니다"),
            @ApiResponse(code = 404, message = "서버 문제 발생"),
            @ApiResponse(code = 500, message = "페이지를 찾을 수 없습니다")
    })
    @ApiOperation(value="그룹 생성(방장)", notes="path variable에는 방장 idx, body에는 이름, 소개, 이미지, 최대인원, 레벨, 목표 분류(선택), 목표치 입력\n" +
            "hostIdx에는 그룹을 생성하려는 유저의 프로필 식별자값 (int)를 넣어주시면 됩니다!!\n" +
            "timetable 예시는 디스코드에 적어두었습니다!")
    public BaseResponse<String> getClubs(Principal principal, @ApiParam(value = "유저 프로필 식별자값") @PathVariable("userProfileIdx") Long userProfileIdx,
                                         @ApiParam(value = "그룹 생성에 필요한 request body 정보")@RequestBody PostClubReq club) {
        try {
            clubService.makesClub(principal, userProfileIdx, club);
            return new BaseResponse<>("그룹 생성을 성공하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹의 모집완료 전환", notes="모집 인원이 모두 다 차면 자동 모집완료 처리 해야합니다.")
    @ResponseBody
    @PatchMapping("/{clubIdx}/recruit-finished")
    public BaseResponse<String> patchRecruitFinished(@PathVariable("clubIdx") Long clubIdx){
        try {
            clubService.updateClubRecruitFinished(clubIdx);
            return new BaseResponse<>("그룹의 모집 상태를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹의 모집중 전환", notes="모집 인원이 모두 다 찬 상황에서 팀원이 나가면 자동 모집중 처리 해야합니다.")
    @ResponseBody
    @PatchMapping("/{clubIdx}/recruit-recruiting")
    public BaseResponse<String> patchRecruitRecruiting(@PathVariable("clubIdx") Long clubIdx){
        try {
            clubService.updateClubRecruitFinished(clubIdx);
            return new BaseResponse<>("그룹의 모집 상태를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 정보 변경", notes="현재 프로필 아이디와 변경하고자 하는 그룹 정보 값을 넘겨주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}")
    public BaseResponse<String> patchClubInfo(@PathVariable("clubIdx") Long clubIdx, @RequestBody PatchClubInfoReq clubInfoReq){
        try {
            clubService.updateClubInfo(clubIdx, clubInfoReq);
            return new BaseResponse<>("그룹의 정보를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ApiOperation(value="그룹 방장 위임", notes="path variable로 클럽 아이디를 전달하고 현재 프로필 아이디, 방장이 될 유저의 프로필 아이디를 body로 전달해주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}/host-change")
    public BaseResponse<String> patchClubHost(Principal principal, @PathVariable("clubIdx") Long clubIdx, @RequestBody PatchHostReq hostReq) {
        try {
            clubService.updateClubHost(principal, clubIdx, hostReq);
            return new BaseResponse<>("방장을 위임 하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
