package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberStatusRepository extends JpaRepository<MemberStatusEntity, Long> {
    Optional<MemberStatusEntity> findMemberStatusEntityByClubIdxAndUserProfileIdx(Long clubIdx, Long ProfileIdx);
}