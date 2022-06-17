package ru.rayovsky.disp.resource;

import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.repository.ParaRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/t-para/")
public class TeacherParaController {
    final ParaRepository paraRepository;
    final AuthCheckService authCheckService;
    public TeacherParaController(ParaRepository paraRepository, AuthCheckService authCheckService) {
        this.paraRepository = paraRepository;
        this.authCheckService = authCheckService;
    }
    //пара для учителя
    @GetMapping("/{id}")
    public List<Para> getUserPara(@PathVariable String id){
        authCheckService.CheckUpperAndId(Long.parseLong(id));
        List<Para> res = paraRepository.findAllByTeacher_UserIdOrderByDateAsc(Long.parseLong(id));
        if (res.size()==0) throw new NothingFoundException("Пары не найдены");
        //прячу от учителя что к нему сейчас придёт диспетчер
        res.forEach(para -> {
            if (para.getState().equals("reserved")){
                para.setDispatcher(null);
                para.setState("pending");
            }
        });
        return res;
    }
    //подсчет пропусков
    @GetMapping("/count/{id}")
    public Integer countMisses(@PathVariable String id){
        authCheckService.CheckUpperAndId(Long.parseLong(id));
        return paraRepository.countAllByTeacher_UserIdAndStateEquals(Long.parseLong(id),"was-not");
    }
}
