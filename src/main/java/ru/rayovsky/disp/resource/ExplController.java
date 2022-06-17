package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.Expl;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.repository.ExplRepository;
import ru.rayovsky.disp.repository.ParaRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/expl/")
public class ExplController {

    private final ExplRepository explRepository;
    private final ParaRepository paraRepository;
    private final AuthCheckService authCheckService;

    public ExplController(ExplRepository explRepository, ParaRepository paraRepository, AuthCheckService authCheckService) {
        this.explRepository = explRepository;
        this.paraRepository = paraRepository;
        this.authCheckService = authCheckService;
    }
    //найти обьяснительную для пары
    @GetMapping("/{id}")
    public Expl getExpl(@PathVariable Long id){
        Expl expl = explRepository.findByPara_Id(id)
                .orElseThrow(()-> new NothingFoundException("Обьяснительная не найдена"));
        authCheckService.CheckUpperAndId(expl.getPara().getTeacher().getUserId());
        return expl;
    }
    //список айди пар к которым обьяснительную уже написали
    @GetMapping("/answered/{id}")
    public List<Long> getAnswered(@PathVariable Long id){
        authCheckService.CheckUpperAndId(id);
        List<Expl> expls = explRepository.findAllByPara_Teacher_UserId(id);
        return expls.stream().map(el -> el.getPara().getId()).collect(Collectors.toList());
    }
    //новая обьяснительная
    @PostMapping("/{paraId}/new")
    public ResponseEntity<Void> newExplanation(@RequestBody Map<String, String> json, @PathVariable Long paraId){
        Para para = paraRepository.findById(paraId)
                .orElseThrow(()->new NothingFoundException("Пара не найдена"));
        authCheckService.CheckRoleAndId(para.getTeacher().getUserId(), "teacher");
        Expl res = new Expl(para, json.get("reason"), LocalDate.now());
        try {
            explRepository.save(res);
        }catch (Exception e){
            throw new IllegalArgumentException("Обьяснительная уже существует");
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
