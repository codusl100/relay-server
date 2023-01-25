package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.dto.TimeTableDTO;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import com.example.relayRun.util.GoalType;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MemberStatusRepository memberStatusRepository;
    private final TimeTableRepository timeTableRepository;

    public ClubService(ClubRepository clubRepository, UserRepository userRepository, UserProfileRepository userProfileRepository,
                       MemberStatusRepository memberStatusRepository, TimeTableRepository timeTableRepository) {

        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.memberStatusRepository = memberStatusRepository;
        this.timeTableRepository = timeTableRepository;
    }

    public List<GetClubListRes> getClubs() throws BaseException {
        try {
            List<GetClubListRes> clubList = clubRepository.findByOrderByRecruitStatusDesc();
            return clubList;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetClubListRes> getClubsByName(String search) throws BaseException {
        try {
            List<GetClubListRes> clubList = clubRepository.findByNameContaining(search);
            return clubList;
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 그룹 생성
    public void makesClub(Principal principal, Long userProfileIdx, PostClubReq club) throws BaseException {
        // 로그인한 유저 userIdx 가져오기
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if(optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        UserEntity userIdx = optional.get();
        // principal의 userIdx랑 userProfileIdx의 userIdx 가 같다면
        Optional<UserProfileEntity> profileOptional = userProfileRepository.findByUserProfileIdx(userProfileIdx);
        if(profileOptional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }
        if(club.getName().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_NAME_EMPTY);
        }
        if(club.getContent().isEmpty()) {
            throw new BaseException(BaseResponseStatus.POST_CLUBS_CONTENTS_EMPTY);
        }
        UserProfileEntity userProfile = profileOptional.get();
        if (userIdx.getUserIdx().equals(userProfile.getUserIdx().getUserIdx())) {
            ClubEntity clubEntity = ClubEntity.builder()
                    .name(club.getName())
                    .content(club.getContent())
                    .imgURL(club.getImgURL())
                    .hostIdx(userProfile)
                    .maxNum(club.getMaxNum())
                    .level(club.getLevel())
                    .goalType(club.getGoalType())
                    .goal(club.getGoal())
                    .build();
            clubRepository.save(clubEntity);

            // host Member Status update
            MemberStatusEntity memberStatusEntity = MemberStatusEntity.builder()
                    .clubIdx(clubEntity)
                    .userProfileIdx(profileOptional.get())
                    .build();

            memberStatusRepository.save(memberStatusEntity);

            // host 시간표 등록
            List<TimeTableDTO> timeTables = club.getTimeTable();

            //1. formatter 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            for (int i = 0; i < timeTables.size(); i++) {
                //2. 입력으로 들어온 string -> local date time으로 변환
                String startStr = timeTables.get(i).getStart();
                String endStr = timeTables.get(i).getEnd();
                LocalDateTime startTime = LocalDateTime.parse(startStr, formatter);
                LocalDateTime endTime = LocalDateTime.parse(endStr, formatter);

                TimeTableEntity timeTableEntity = TimeTableEntity.builder()
                        .memberStatusIdx(memberStatusEntity)
                        .day(timeTables.get(i).getDay())
                        .start(startTime)
                        .end(endTime)
                        .goal(timeTables.get(i).getGoal())
                        .goalType(timeTables.get(i).getGoalType())
                        .build();

                timeTableRepository.save(timeTableEntity);
            }
        }


    }
}
