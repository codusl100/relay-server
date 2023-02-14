package com.example.relayRun.fcm.repository;

import com.example.relayRun.fcm.entity.UserDeviceEntity;
import com.example.relayRun.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {
//    List<UserDeviceEntity> findAllByUserIdx_UserIdx(List<Long> ids);
    List<UserDeviceEntity> findAllByUserIdx_UserIdx(Long id);

    Optional<UserDeviceEntity> findByUserDeviceTokenAndUserIdx(String token, UserEntity user);
}
