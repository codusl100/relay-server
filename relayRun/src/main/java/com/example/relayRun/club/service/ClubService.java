package com.example.relayRun.club.service;

import com.example.relayRun.club.dto.GetMemberOfClubRes;
import com.example.relayRun.club.dto.PostClubReq;
import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.dto.GetMemberProfileRes;
import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.user.repository.UserRepository;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MemberStatusRepository memberStatusRepository;

    public ClubService(ClubRepository clubRepository, UserRepository userRepository, UserProfileRepository userProfileRepository, MemberStatusRepository memberStatusRepository) {

        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.memberStatusRepository = memberStatusRepository;
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

    public List<GetMemberOfClubRes> getMemberOfClub(Long clubIdx) throws BaseException {
        try {
            List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findAllByClubIdx_ClubIdxAndApplyStatus(clubIdx, "ACCEPTED");
            if(memberStatusEntityList.isEmpty()) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
            }
            List<GetMemberOfClubRes> res = new ArrayList<>();
            for(MemberStatusEntity memberStatusEntity : memberStatusEntityList) {
                UserProfileEntity userProfileEntity = memberStatusEntity.getUserProfileIdx();
                GetMemberProfileRes getMemberProfileRes = GetMemberProfileRes.builder()
                        .userProfileIdx(userProfileEntity.getUserProfileIdx())
                        .nickname(userProfileEntity.getNickName())
                        .statusMsg(userProfileEntity.getStatusMsg())
                        .imgUrl(userProfileEntity.getImgURL())
                        .build();
                GetMemberOfClubRes getMemberOfClubRes = GetMemberOfClubRes.builder()
                        .memberStatusIdx(memberStatusEntity.getMemberStatusIdx())
                        .userProfile(getMemberProfileRes)
                        .build();
                res.add(getMemberOfClubRes);
            }
            return res;
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
        }
        else {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EQUALS);
        }
    }
}
