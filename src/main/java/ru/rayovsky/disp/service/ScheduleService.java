package ru.rayovsky.disp.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.model.ParaPrototype;
import ru.rayovsky.disp.model.ScheduleHelper;
import ru.rayovsky.disp.repository.ParaPrototypeRepository;
import ru.rayovsky.disp.repository.ParaRepository;
import ru.rayovsky.disp.repository.ScheduleHelperRepository;
import ru.rayovsky.disp.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduleService implements Runnable{
    private final ParaRepository paraRepository;
    private final ParaPrototypeRepository paraPrototypeRepository;
    private final UserRepository userRepository;
    private final ScheduleHelperRepository scheduleHelperRepository;

    public ScheduleService(ParaRepository paraRepository, ParaPrototypeRepository paraPrototypeRepository,
                           UserRepository userRepository, ScheduleHelperRepository scheduleHelperRepository){
        this.paraRepository = paraRepository;
        this.paraPrototypeRepository = paraPrototypeRepository;
        this.userRepository = userRepository;
        this.scheduleHelperRepository = scheduleHelperRepository;
    }
    public Para paraBuilder(ParaPrototype p){
        return new Para(p.getType(), p.getAudit(), LocalDate.now(),
                p.getNum(),"pending",p.getTeacher(),p.getSubject());
    }
    public List<Para> paraListBuilder(Long userId, DayOfWeek day){
        List<ParaPrototype> ps =paraPrototypeRepository.findAllByTeacher_UserIdAndDayAndActiveIsTrue(userId, day);
        List<Para> res = new ArrayList<>();
        ps.forEach(p->res.add(paraBuilder(p)));
        return res;
    }

    public void buildTodayParasById(Long id){
        List<Para> res = paraListBuilder(id,LocalDate.now().getDayOfWeek());
        paraRepository.saveAll(res);
    }

    public ResponseEntity<Void> buildAllTodayParas(){
        userRepository.findAllByRole("teacher").forEach(user -> buildTodayParasById(user.getUserId()));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startScheduler(){
        run();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);
        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::run,
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        ScheduleHelper lastDate = scheduleHelperRepository.findById(1L)
                .orElseThrow(()->new NothingFoundException("Не удалось получить предыдущую дату создания расписания"));
        if(lastDate.getUpdateDate().compareTo(LocalDate.now()) < 0){
            buildAllTodayParas();
            lastDate.setUpdateDate(LocalDate.now());
            scheduleHelperRepository.save(lastDate);
        }
        else
            System.out.println("Already built");
    }
}
