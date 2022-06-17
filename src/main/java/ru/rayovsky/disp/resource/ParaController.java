package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.ParaRepository;
import ru.rayovsky.disp.repository.UserRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/para/")
public class ParaController {
    private final ParaRepository paraRepository;
    private final UserRepository userRepository;
    private final AuthCheckService authCheckService;

    public ParaController(ParaRepository paraRepository, UserRepository userRepository, AuthCheckService authCheckService) {
        this.paraRepository = paraRepository;
        this.userRepository = userRepository;
        this.authCheckService = authCheckService;
    }

    @GetMapping("/")
    public List<List<Para>> getAllParas(){
        authCheckService.CheckAuthByRole();
        List<List<Para>> res = new ArrayList<>();
        IntStream.range(1,6).forEach(i->res.add(paraRepository.findAllByNumAndDate(i,LocalDate.now())));
        return res;
    }

    @PutMapping("/{id}/state/")
    public ResponseEntity<HttpStatus> setState(@PathVariable Long id, @RequestBody Map<String, String> json){
        authCheckService.CheckAuthByRole();
        //checks
        if(json.get("state") ==null || json.get("hour") ==null ||  json.get("minute") ==null)
            throw new NothingFoundException("Заданы не все параметры");

        System.out.println("hey");
        System.out.println(json.get("hour") + ":" + json.get("minute"));
        try {
            Para para = paraRepository.findById(id)
                    .orElseThrow(()->new NothingFoundException("Пара не найдена"));
            if (para.getDispatcher() == null){
                User dis = userRepository.findById(Long.parseLong(json.get("disId")))
                        .orElseThrow(()->new NothingFoundException("Пользователь не найден"));
                //TODO: чекать роль диспечера
                para.setDispatcher(dis);
            }
            if(para.getDispatcher().getUserId()==Long.parseLong(json.get("disId"))){
                para.setState(json.get("state"));
                para.setCheckDate(LocalTime.of(Integer.parseInt(json.get("hour")),
                        Integer.parseInt(json.get("minute"))));

                paraRepository.save(para);
            }else throw  new AuthorizationException("Другой диспетчер уже отвечает за данную пару");

        }catch (NoSuchElementException e){throw new NothingFoundException("Пара не найдена");}
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/{id}")
    public Para getParaById(@PathVariable Long id){
        Para para = paraRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Пара не найдена"));
        authCheckService.CheckAuthById(para.getTeacher().getUserId());
        return para;
    }
}
