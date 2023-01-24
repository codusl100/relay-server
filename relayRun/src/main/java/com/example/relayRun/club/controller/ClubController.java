package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.dto.PatchClubRecruitStatusReq;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.user.dto.PatchUserPwdReq;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/clubs")
public class ClubController {

    private ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }


    @ApiOperation(value="그룹 목록 조회(전체, 검색)", notes="URI 뒤에 search parameter로 그룹 이름을 검색할 수 있다. 아무것도 넘기지 않을 경우 전체 목록이 조회된다.")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetClubListRes>> getClubs(@RequestParam(required = false) String search) {
        try {
            List<GetClubListRes> clubList;
            if(search == null) {
                clubList = clubService.getClubs();
            } else {
                clubList = clubService.getClubsByName(search);
            }
            return new BaseResponse<>(clubList);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @ApiOperation(value="그룹의 모집 상태 변경", notes="현재 프로필 아이디와 변경하고자 하는 모집 상태 값을 넘겨주세요")
    @ResponseBody
    @PatchMapping("/{clubIdx}/recruit-change")
    public BaseResponse<String> changeRecruitStatus(@PathVariable("clubIdx") Long clubIdx, @RequestBody PatchClubRecruitStatusReq clubRecruitStatusReq){
        try {
            clubService.changeClubRecruitStatus(clubIdx, clubRecruitStatusReq);
            return new BaseResponse<>("그룹의 모집 상태를 변경하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

}
