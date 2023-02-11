package com.example.relayRun.schedule;

import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.club.repository.TimeTableRepository;
import com.example.relayRun.event.NotifyEventPublisher;
import com.example.relayRun.schedule.task.NotifyToRun;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.checkerframework.checker.units.qual.C;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.*;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class ScheduleService {
    private TimeTableRepository timeTableRepository;
    private TaskScheduler taskScheduler;
    private NotifyEventPublisher publisher;

    private List<ScheduledFuture<CronTask>> scheduledCronTasks;
    private List<CronTask> enrolledCronTasks;
    public ScheduleService(TimeTableRepository timeTableRepository,
                           TaskScheduler taskScheduler,
                           NotifyEventPublisher publisher) {
        this.timeTableRepository = timeTableRepository;
        this.taskScheduler = taskScheduler;
        this.publisher = publisher;
        this.scheduledCronTasks = new ArrayList<>();
    }
    private String toCronString(LocalTime time, int day) {
        String ret = time.getSecond() + " "
                + time.getMinute() + " "
                + time.getHour() + " "
                + "1-31 1-12 "
                + day;
        return ret;
    }

    public void  scheduleTimeTable(TimeTableEntity timetable) {
        NotifyToRun task = new NotifyToRun(timetable, publisher);
        CronTrigger trigger =  new CronTrigger(toCronString(timetable.getStart(), timetable.getDay()));
        CronTask cronTask = new CronTask(task, trigger);
        this.enrolledCronTasks.add(cronTask);
        this.scheduledCronTasks.add((ScheduledFuture<CronTask>) taskScheduler.schedule(task, trigger));
    }
    @PostConstruct
    public void scheduleStoredTimetable() {
        List<TimeTableEntity> storedTimeTable = timeTableRepository.findAll();
        storedTimeTable.forEach(timetable-> {
                log.info(toCronString(timetable.getStart(), timetable.getDay()));
                scheduleTimeTable(timetable);
            }
        );
        log.info(scheduledCronTasks.size() + "");
    }
}
