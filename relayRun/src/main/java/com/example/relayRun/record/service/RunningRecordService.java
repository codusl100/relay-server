package com.example.relayRun.record.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.record.dto.PostRunningFinishReq;
import com.example.relayRun.record.dto.PostRunningFinishRes;
import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.LocationRepository;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.record.util.RecordDataHandler;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class RunningRecordService {
    UserProfileRepository userProfileRepository;
    RunningRecordRepository runningRecordRepository;
    LocationRepository locationRepository;
    MemberStatusRepository memberStatusRepository;

    UserRepository userRepository;
    TimeTableRepository timeTableRepository;
    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository,
                                LocationRepository locationRepository,
                                MemberStatusRepository memberStatusRepository,
                                TimeTableRepository timeTableRepository,
                                UserProfileRepository userProfileRepository,
                                UserRepository userRepository
    ) {
        this.runningRecordRepository = runningRecordRepository;
        this.locationRepository = locationRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.timeTableRepository = timeTableRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public PostRunningInitRes startRunning(Principal principal, PostRunningInitReq runningInitReq) throws BaseException {
        Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(
                runningInitReq.getProfileIdx(),
                "ACCEPTED"
        );
        if (optionalMemberStatus.isEmpty()){
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
        }
        Optional<UserProfileEntity> optionalUserProfile = userProfileRepository.findById(runningInitReq.getProfileIdx());
        if (optionalUserProfile.isEmpty())
            throw new BaseException(BaseResponseStatus.POST_RECORD_NO_PROFILE_IDX);
        UserProfileEntity userProfileParam = optionalUserProfile.get();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        UserEntity userEntityPrincipal = optionalUser.get();
        if (!userEntityPrincipal.getUserIdx().equals(userProfileParam.getUserIdx().getUserIdx()))
            throw new BaseException(BaseResponseStatus.POST_RECORD_NOT_MATCH_PARAM_PRINCIPAL);
        MemberStatusEntity memberStatus = optionalMemberStatus.get();
        RunningRecordEntity recordEntity = new RunningRecordEntity();
        recordEntity.setMemberStatusIdx(memberStatus);
        recordEntity.setDistance(0.0f);
        recordEntity.setTime(0.0f);
        recordEntity.setPace(0.0f);
        recordEntity = runningRecordRepository.save(recordEntity);
        PostRunningInitRes result = new PostRunningInitRes();
        result.setRunningRecordIdx(recordEntity.getRunningRecordIdx());
        return result;
    }

    public PostRunningFinishRes finishRunning(Principal principal, PostRunningFinishReq runningFinishReq) throws BaseException {
        Optional<RunningRecordEntity> oldOptionalRecord = runningRecordRepository.findById(runningFinishReq.getRunningRecordIdx());
        if (oldOptionalRecord.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_RECORD_ID);
        }
        RunningRecordEntity oldRecord = oldOptionalRecord.get();
        Optional<UserEntity> optionalUserPrincipal = userRepository.findByEmail(principal.getName());
        if (optionalUserPrincipal.isEmpty())
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        UserEntity userPrincipal = optionalUserPrincipal.get();
        try {
            if (!oldRecord.getMemberStatusIdx().getUserProfileIdx().getUserIdx().equals(userPrincipal))
                throw new BaseException(BaseResponseStatus.POST_RECORD_NOT_MATCH_PARAM_PRINCIPAL);
            List<LocationEntity> locations = RecordDataHandler.toEntityList(runningFinishReq.getLocations());
            LocalTime timeFormat = runningFinishReq.getTime();
            Optional<TimeTableEntity> optionalTimeTable = timeTableRepository
                    .findByMemberStatusIdxAndDayAndStartLessThanEqualAndEndGreaterThanEqual(
                    oldRecord.getMemberStatusIdx(),
                    RecordDataHandler.toIntDay(locations.get(0).getTime().getDayOfWeek()),
                    locations.get(0).getTime().toLocalTime(),
                    locations.get(locations.size() - 1).getTime().toLocalTime()
            );
            if (optionalTimeTable.isEmpty())
                throw new BaseException(BaseResponseStatus.POST_RECORD_NO_TIMETABLE);
            // calculate success, running time
            TimeTableEntity timeTable = optionalTimeTable.get();
            if (oldRecord.getRunningStatus().equals("finish")) {
                throw new BaseException(BaseResponseStatus.POST_RECORD_ALREADY_FINISH);
            }
            Float seconds = RecordDataHandler.toSecond(timeFormat);
            String isSuccess =  RecordDataHandler.isSuccess(
                    timeTable.getGoalType(),
                    timeTable.getGoal(),
                    seconds,
                    runningFinishReq.getPace(),
                    runningFinishReq.getDistance()
            );
            // update entity
            oldRecord.setDistance(runningFinishReq.getDistance());
            oldRecord.setPace(runningFinishReq.getPace());
            oldRecord.setTime(seconds);
            oldRecord.setRunningStatus("finish");
            oldRecord.setGoalStatus(isSuccess);
            for (LocationEntity location : locations) {
                location.setRecordIdx(oldRecord);
            }
            locationRepository.saveAll(locations);
            runningRecordRepository.save(oldRecord);
            return new PostRunningFinishRes(isSuccess);
        } catch (ParseException e) {
            throw new BaseException(BaseResponseStatus.POST_PARSE_ERROR);
        }
    }
}
