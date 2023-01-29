package com.example.relayRun.club.repository;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    Optional<ClubEntity> findByClubIdx(Long clubIdx);
    List<GetClubListRes> findByOrderByRecruitStatusDesc();
    List<ClubEntity> findAll();
    List<GetClubListRes> findByNameContaining(String search);
    Optional<ClubEntity> findByHostIdx(UserProfileEntity hostIdx);
    Optional<ClubEntity> findByClubIdxAndStatus(Long id, String status);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update ClubEntity c set c.recruitStatus  = :recruitStatus where c.clubIdx = :clubIdx")
    int updateRecruitStatus(@Param(value="recruitStatus") String recruitStatus, @Param(value="clubIdx") Long clubIdx);
}
