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
        authCheckService.checkUpper();
        List<List<Para>> res = new ArrayList<>();

        IntStream.range(1,6).forEach(i->res.add(paraRepository.findAllByNumAndDate(i,LocalDate.now())));

        return res;
    }
    //поменять стейт
    @PutMapping("/{id}/state/")
    public ResponseEntity<HttpStatus> setState(@PathVariable Long id, @RequestBody Map<String, String> json){
        //парс джсона
        Long disID = Long.parseLong(json.get("disId"));
        LocalTime time =  LocalTime.of(Integer.parseInt(json.get("hour")),
                Integer.parseInt(json.get("minute")));
        String state = json.get("state");

        //проверки
        authCheckService.checkRoleAndId(disID, "dispatcher");
        checkState(state);
        Para para = paraRepository.findById(id).orElseThrow(()->new NothingFoundException("Пара не найдена"));
        checkTime(time, para);

        //если нет ответственного то ставится текущий диспечер
        if (para.getDispatcher() == null){
            User dis = userRepository.findById(disID)
                    .orElseThrow(()->new NothingFoundException("Пользователь не найден"));
            para.setDispatcher(dis);
        }

        //если диспечер ответственен за пару то менятся стейт, иначе - ошибка
        if(para.getDispatcher().getUserId()==disID){
            para.setState(state);
            para.setCheckDate(time);
            paraRepository.save(para);
        }else throw  new AuthorizationException("Другой диспетчер уже отвечает за данную пару");

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    //пара по айди
    @GetMapping("/{id}")
    public Para getParaById(@PathVariable Long id){
        Para para = paraRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Пара не найдена"));
        authCheckService.checkUpperAndId(para.getTeacher().getUserId());

        return para;
    }
    //проверки
    public void checkTime(LocalTime time, Para para){
        ParaTime inter = paraTimeRepository.findByNum(para.getNum());
        if (!time.isAfter(inter.getStartTime().minus(1,ChronoUnit.SECONDS))||
                !time.isBefore(inter.getEndTime().plus(1, ChronoUnit.SECONDS))) {
            throw new IllegalArgumentException("заданное время не соответствует времени проведения пары");
        }
    }
    public void checkState(String state){
        if(!Arrays.asList("pending", "was", "was-not", "reserved").contains(state))
            throw new IllegalArgumentException("не верно задан параметр state");
    }


}
