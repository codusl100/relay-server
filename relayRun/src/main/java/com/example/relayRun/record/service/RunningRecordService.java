package com.example.relayRun.record.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RunningRecordService {
    RunningRecordRepository runningRecordRepository;
    MemberStatusRepository memberStatusRepository;
    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository,
                                MemberStatusRepository memberStatusRepository) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberStatusRepository = memberStatusRepository;
    }

    public PostRunningInitRes startRunning(PostRunningInitReq runningInitReq) throws BaseException {
        Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository.findByClubIdx_ClubIdxAndUserProfileIdx_UserProfileIdx(
                runningInitReq.getClubIdx(),
                runningInitReq.getProfileIdx()
        );
        if (optionalMemberStatus.isEmpty()){
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
        }
        MemberStatusEntity memberStatus = optionalMemberStatus.get();
        RunningRecordEntity recordEntity = RunningRecordEntity.builder()
                .memberStatusIdx(memberStatus)
                .distance(0.0f).time(0.0f).pace(0.0f)
                .build();
        recordEntity = runningRecordRepository.save(recordEntity);
        PostRunningInitRes result = new PostRunningInitRes();
        result.setRunningRecordIdx(recordEntity.getRunningRecordIdx());
        return result;
    }
}
