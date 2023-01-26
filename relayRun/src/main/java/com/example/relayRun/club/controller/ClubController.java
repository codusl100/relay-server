package com.example.relayRun.club.controller;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.dto.GetMemberOfClubRes;
import com.example.relayRun.club.service.ClubService;
import com.example.relayRun.club.service.MemberStatusService;
import com.example.relayRun.record.service.RecordService;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/clubs")
public class ClubController {

    private ClubService clubService;
    private MemberStatusService memberStatusService;
    private RecordService recordService;

    public ClubController(ClubService clubService, MemberStatusService memberStatusService, RecordService recordService) {
        this.clubService = clubService;
        this.memberStatusService = memberStatusService;
        this.recordService = recordService;
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

    @ApiOperation(value="그룹 멤버 관련 정보 조회", notes="path variable로 그룹 아이디, query string으로 현재 날짜 전달해주세요")
    @ResponseBody
    @GetMapping("/{clubIdx}")
    public BaseResponse<List<GetMemberOfClubRes>> getMemberOfClub(@PathVariable Long clubIdx, @RequestParam("date") String date) {
        try {
            List<GetMemberOfClubRes> getMemberOfClubResList = clubService.getMemberOfClub(clubIdx);
            for(GetMemberOfClubRes getMemberOfClubRes : getMemberOfClubResList) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", formatter);
                LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", formatter);
                getMemberOfClubRes.setRunningRecord(recordService.getRecordWithoutLocation(getMemberOfClubRes.getMemberStatusIdx(), startDate, endDate));
                getMemberOfClubRes.setTimeTable(memberStatusService.getTimeTablesByMemberStatusIdx(getMemberOfClubRes.getMemberStatusIdx()));
            }
            return new BaseResponse<>(getMemberOfClubResList);
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
