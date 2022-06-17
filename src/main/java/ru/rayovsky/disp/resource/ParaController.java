package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.model.ParaTime;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.ParaRepository;
import ru.rayovsky.disp.repository.ParaTimeRepository;
import ru.rayovsky.disp.repository.UserRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/para/")
public class ParaController {
    private final ParaRepository paraRepository;
    private final UserRepository userRepository;
    private final AuthCheckService authCheckService;
    private final ParaTimeRepository paraTimeRepository;

    public ParaController(ParaRepository paraRepository, UserRepository userRepository, AuthCheckService authCheckService, ParaTimeRepository paraTimeRepository) {
        this.paraRepository = paraRepository;
        this.userRepository = userRepository;
        this.authCheckService = authCheckService;
        this.paraTimeRepository = paraTimeRepository;
    }
    //все пары на сегодня
    @GetMapping("/")
    public List<List<Para>> getAllTodayParas(){
        authCheckService.CheckUpper();
        List<List<Para>> res = new ArrayList<>();
        IntStream.range(1,6).forEach(i->res.add(paraRepository.findAllByNumAndDate(i,LocalDate.now())));
        return res;
    }
    //поменять стейт (облегчи метод)
    @PutMapping("/{id}/state/")
    public ResponseEntity<HttpStatus> setState(@PathVariable Long id, @RequestBody Map<String, String> json){
        //TODO: сначала все распарсить а вместо постоянных гетов
        //TODO: давать опшианалы в репозиториях
        authCheckService.CheckRoleAndId(Long.parseLong(json.get("disId")), "dispatcher");
        LocalTime time =  LocalTime.of(Integer.parseInt(json.get("hour")),
                Integer.parseInt(json.get("minute")));
        //checks
        if(json.get("state") == null || json.get("hour") ==null ||  json.get("minute") ==null)
            throw new NothingFoundException("Заданы не все параметры");
        if(!Arrays.asList("pending", "was", "was-not", "reserved").contains(json.get("state")))
            throw new IllegalArgumentException("не верно задан параметр state");

        try {
            Para para = paraRepository.findById(id)
                    .orElseThrow(()->new NothingFoundException("Пара не найдена"));

            ParaTime inter = paraTimeRepository.findByNum(para.getNum());

            if (!time.isAfter(inter.getStartTime().minus(1,ChronoUnit.SECONDS))||
                    !time.isBefore(inter.getEndTime().plus(1, ChronoUnit.SECONDS))) {
                throw new IllegalArgumentException("заданное время не соответствует времени проведения пары");
            }

            if (para.getDispatcher() == null){
                User dis = userRepository.findById(Long.parseLong(json.get("disId")))
                        .orElseThrow(()->new NothingFoundException("Пользователь не найден"));
                para.setDispatcher(dis);
            }
            if(para.getDispatcher().getUserId()==Long.parseLong(json.get("disId"))){
                para.setState(json.get("state"));
                para.setCheckDate(time);

                paraRepository.save(para);
            }else throw  new AuthorizationException("Другой диспетчер уже отвечает за данную пару");

        }catch (NoSuchElementException e){throw new NothingFoundException("Пара не найдена");}
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    //пара по айди
    @GetMapping("/{id}")
    public Para getParaById(@PathVariable Long id){
        Para para = paraRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Пара не найдена"));
        authCheckService.CheckUpperAndId(para.getTeacher().getUserId());
        return para;
    }

}
