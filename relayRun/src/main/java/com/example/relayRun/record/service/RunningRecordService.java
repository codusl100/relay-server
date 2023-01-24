package com.example.relayRun.record.service;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.record.dto.PostRunningFinishReq;
import com.example.relayRun.record.dto.PostRunningInitReq;
import com.example.relayRun.record.dto.PostRunningInitRes;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.record.repository.LocationRepository;
import com.example.relayRun.record.repository.RunningRecordRepository;
import com.example.relayRun.record.util.RecordDataHandler;
import com.example.relayRun.timetable.entity.TimeTableEntity;
import com.example.relayRun.timetable.repository.TimeTableRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class RunningRecordService {
    RunningRecordRepository runningRecordRepository;
    LocationRepository locationRepository;
    MemberStatusRepository memberStatusRepository;

    TimeTableRepository timeTableRepository;
    @Autowired
    public RunningRecordService(RunningRecordRepository runningRecordRepository,
                                LocationRepository locationRepository,
                                MemberStatusRepository memberStatusRepository,
                                TimeTableRepository timeTableRepository) {
        this.runningRecordRepository = runningRecordRepository;
        this.locationRepository = locationRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.timeTableRepository = timeTableRepository;
    }

    public PostRunningInitRes startRunning(PostRunningInitReq runningInitReq) throws BaseException {
        Optional<MemberStatusEntity> optionalMemberStatus = memberStatusRepository.findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(
                runningInitReq.getProfileIdx(),
                "ACCEPTED"
        );
        if (optionalMemberStatus.isEmpty()){
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_CLUB_ACCESS);
        }
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

    public String finishRunning(PostRunningFinishReq runningFinishReq) throws BaseException {
        Optional<RunningRecordEntity> oldOptionalRecord = runningRecordRepository.findById(runningFinishReq.getRunningRecordIdx());
        if (oldOptionalRecord.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_RECORD_INVALID_RECORD_ID);
        }
        try {
            RunningRecordEntity oldRecord = oldOptionalRecord.get();
            List<LocationEntity> locations = RecordDataHandler.toEntityList(runningFinishReq.getLocations());
            LocalTime timeFormat = LocalTime.parse(
                    runningFinishReq.getTime(), DateTimeFormatter.ofPattern("HH:mm:ss.nnn")
            );
            Optional<TimeTableEntity> optionalTimeTable = timeTableRepository
                    .findByMemberStatusIdxAndDayAndStartLessThanEqualAndEndGreaterThanEqual(
                    oldRecord.getMemberStatusIdx(),
                    RecordDataHandler.toIntDay(locations.get(0).getTime().getDayOfWeek()),
                    locations.get(0).getTime().toLocalTime(),
                    locations.get(locations.size() - 1).getTime().toLocalTime()
            );
            if (optionalTimeTable.isEmpty()) {
                throw new BaseException(BaseResponseStatus.POST_RECORD_NO_TIMETABLE);
            }
            TimeTableEntity timeTable = optionalTimeTable.get();
            Float seconds = RecordDataHandler.toSecond(timeFormat);
            oldRecord.setDistance(runningFinishReq.getDistance());
            oldRecord.setPace(runningFinishReq.getPace());
            oldRecord.setTime(seconds);
            oldRecord.setRunningStatus("finish");
            oldRecord.setGoalStatus(
                    RecordDataHandler.isSuccess(
                    timeTable.getGoalType(),
                    timeTable.getGoal(),
                    seconds,
                    runningFinishReq.getPace(),
                    runningFinishReq.getDistance()
                )
            );
            for (LocationEntity location : locations) {
                location.setRecordIdx(oldRecord);
            }
            locationRepository.saveAll(locations);
            runningRecordRepository.save(oldRecord);
        } catch (ParseException e) {
            throw new BaseException(BaseResponseStatus.POST_PARSE_ERROR);
        }
        return "레코드 저장 성공";
    }
}
