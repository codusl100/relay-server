package com.example.relayRun.schedule;

import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.event.NotifyEventPublisher;
import com.example.relayRun.event.TimeToRunEvent;
import com.example.relayRun.schedule.task.NotifyToRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ContextLifecycleScheduledTaskRegistrar;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ScheduleComponent {
    private TimeTableRepository timeTableRepository;
    private ScheduledTaskRegistrar scheduledTaskRegistrar;
    private NotifyEventPublisher publisher;

    public ScheduleComponent(TimeTableRepository timeTableRepository,
                             ScheduledTaskRegistrar scheduledTaskRegistrar,
                             NotifyEventPublisher publisher) {
        this.timeTableRepository = timeTableRepository;
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scanTimeTable() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<TimeTableEntity> timeTables = timeTableRepository.findAllByDay(today.getDayOfWeek().getValue());
        if (timeTables.isEmpty()) {
            log.info("no timetable entity today");
        }else {
            log.info("reserve notify actions");
            timeTables.forEach(timetable-> {
                if (now.isAfter(timetable.getStart()))
                    return ;
                log.info(timetable.getStart().toString());
               scheduledTaskRegistrar.getScheduler().schedule(
                        new NotifyToRun(timetable,publisher),
                        Timestamp.valueOf(LocalDateTime.of(today, timetable.getStart()))
                );
            });
        }
    }
}
