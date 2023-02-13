package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    List<ClubEntity> findByNameContainingAndStatusOrderByCreatedAtDesc(String search, String status);
    List<ClubEntity> findByStatusOrderByCreatedAtDesc(String Status);
    Optional<ClubEntity> findByClubIdxAndStatus(Long id, String status);
    Optional<ClubEntity> findByClubIdxAndRecruitStatusAndStatus(Long id, String recruitStatus, String status);

//    Optional<ClubEntity> findByClubIdx(Long clubIdx);
//    List<GetClubListRes> findByOrderByRecruitStatusDesc();
//    List<ClubEntity> findAll();
//    List<GetClubListRes> findByNameContaining(String search);
}
