package com.example.relayRun.timetable.service;

import com.example.relayRun.club.dto.PostMemberStatusReq;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.timetable.entity.TimeTableEntity;
import com.example.relayRun.timetable.repository.TimeTableRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;
    private MemberStatusRepository memberStatusRepository;

    public TimeTableService(TimeTableRepository timeTableRepository) {
        this.timeTableRepository = timeTableRepository;
    }

    //시간표 등록
    public void createTimeTable(PostMemberStatusReq memberStatus) throws BaseException {
        try {
            Long memberStatusIdx = memberStatus.getMemberStatusIdx();
            Optional<MemberStatusEntity> memberStatusEntity = memberStatusRepository.findById(memberStatusIdx);

            TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                    .memberStatusIdx(memberStatusEntity.get())
                    .day(memberStatus.getDay())
                    .start(memberStatus.getStart())
                    .end(memberStatus.getEnd())
                    .goal(memberStatus.getGoal())
                    .goalType(memberStatus.getGoalType())
                    .build();

            timeTableRepository.save(timeTableEntity);
        } catch(Exception e) {
            throw new BaseException(BaseResponseStatus.POST_TIMETABLE_FAIL);
        }
    }
}
