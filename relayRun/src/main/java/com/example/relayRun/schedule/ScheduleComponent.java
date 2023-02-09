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
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
public class ScheduleComponent {
    private TimeTableRepository timeTableRepository;
    private TaskScheduler taskScheduler;
    private NotifyEventPublisher publisher;

    public ScheduleComponent(TimeTableRepository timeTableRepository,
                             TaskScheduler taskScheduler,
                             NotifyEventPublisher publisher) {
        this.timeTableRepository = timeTableRepository;
        this.taskScheduler = taskScheduler;
        this.publisher = publisher;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void scanTimeTable() {
        LocalDateTime today = LocalDateTime.now();
        List<TimeTableEntity> timeTables = timeTableRepository.findAllByDay(today.getDayOfWeek().getValue());
        if (timeTables.isEmpty()) {
            log.info("no timetable entity today");
        }else {
            log.info("reserve notify actions");
            timeTables.forEach(timetable-> {
                if (today.toLocalTime().isAfter(timetable.getStart()))
                    return ;
                log.info(timetable.getStart().toString());
                Duration delay = Duration.between(today.toLocalTime(), timetable.getStart());
                log.info("after " + Duration.between(timetable.getStart(),today.toLocalTime()).getSeconds() + " run event is published");
                taskScheduler.scheduleWithFixedDelay(new NotifyToRun(timetable,publisher), delay);
            });
        }
    }
}
