package com.example.relayRun.user.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.repository.MemberStatusRepository;
import com.example.relayRun.user.dto.GetProfileRes;
import com.example.relayRun.user.dto.GetUserProfileClubRes;
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
public class UserProfileService {

    private MemberStatusRepository memberStatusRepository;
    private UserProfileRepository userProfileRepository;
    private UserRepository userRepository;

    public UserProfileService(MemberStatusRepository memberStatusRepository, UserProfileRepository userProfileRepository,
                              UserRepository userRepository) {
        this.memberStatusRepository = memberStatusRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public GetUserProfileClubRes getUserProfileClub(Long userProfileIdx) throws BaseException {
        ClubEntity clubEntity = null;
        List<MemberStatusEntity> memberStatusEntityList = memberStatusRepository.findByUserProfileIdx_UserProfileIdx(userProfileIdx);
        for (MemberStatusEntity memberStatusEntity : memberStatusEntityList) {
            if (memberStatusEntity.getApplyStatus().equals("ACCEPTED")) {
                clubEntity = memberStatusEntity.getClubIdx();
                break;
            }
        }
        if (clubEntity != null)
            return new GetUserProfileClubRes(clubEntity.getClubIdx(), clubEntity.getName());
        throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
    }

    public GetProfileRes getUserProfile(Principal principal, Long profileIdx) throws BaseException {
        Optional<UserEntity> optional = userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        // userProfileIdx 존재 안할때
        UserProfileEntity userProfileList = userProfileRepository.findByUserProfileIdx(profileIdx).get();
        if (userProfileList == null) {
            throw new BaseException(BaseResponseStatus.POST_USERS_PROFILES_EMPTY);
        }
        GetProfileRes userProfile = new GetProfileRes();
        userProfile.setUserProfileIdx(profileIdx);
        userProfile.setNickname(userProfileList.getNickName());
        userProfile.setStatusMsg(userProfileList.getStatusMsg());
        userProfile.setIsAlarmOn(userProfileList.getIsAlarmOn());
        userProfile.setImgUrl(userProfileList.getImgURL());
        userProfile.setUserName(userProfileList.getUserIdx().getName());
        userProfile.setEmail(userProfileList.getUserIdx().getEmail());
        return userProfile;
    }
}
