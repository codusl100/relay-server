package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeTableRepository extends JpaRepository<TimeTableEntity, Long> {
    @Query(value = "select time_table_idx from time_table left join member_status on time_table.member_status_idx = member_status.member_status_idx where member_status.club_idx = :clubIdx and member_status.apply_status = 'ACCEPTED' and time_table.day = :day and ((:start between time_table.start and time_table.end) or (:end between time_table.start and time_table.end) or (:start <= time_table.start and :end >= time_table.end))", nativeQuery = true)
    List<Long> selectDuplicateTimeTable(@Param(value = "clubIdx") Long clubIdx,
                                        @Param(value = "day") Integer day,
                                        @Param(value = "start") LocalTime start,
                                        @Param(value = "end") LocalTime end);
    List<TimeTableEntity> findByMemberStatusIdx_MemberStatusIdx(Long memberStatusIdx);
    Optional<TimeTableEntity> findByMemberStatusIdxAndDayAndStartLessThanEqualAndEndGreaterThanEqual(
            MemberStatusEntity memberStatusIdx,
            int day, LocalTime start, LocalTime end
    );

    List<TimeTableEntity> findAllByDay(int day);
    Optional<TimeTableEntity> findByMemberStatusIdxAndDay(MemberStatusEntity memberStatus, int day);
    Optional<TimeTableEntity> findByMemberStatusIdx_MemberStatusIdxAndDay(Long memberStatusIdx, int day);

    @Query(value = "select tt.time_table_idx,tt.created_at, tt.updated_at,\n" +
            "       tt.day, tt.end, tt.start, tt.goal_type, tt.goal, tt.member_status_idx, tt.status  from time_table tt\n" +
            "left join member_status ms on tt.member_status_idx = ms.member_status_idx\n" +
            "where tt.day = :day\n" +
            "AND ms.club_idx = :clubIdx\n" +
            "AND tt.start >= :end\n" +
            "order by tt.start limit 1",nativeQuery = true)
    Optional<TimeTableEntity> findByClubAndDayAndAfterTime(@Param(value = "clubIdx") Long clubIdx,
                                                           @Param(value = "day") int day,
                                                           @Param(value = "end") String end);
}
