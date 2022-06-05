package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.model.Expl;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.repository.ExplRepository;
import ru.rayovsky.disp.repository.ParaRepository;

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

    public ExplController(ExplRepository explRepository, ParaRepository paraRepository) {
        this.explRepository = explRepository;
        this.paraRepository = paraRepository;
    }
    @GetMapping("/{id}")
    public Expl getAllParas(@PathVariable Long id){
        return explRepository.findByPara_Id(id);
    }
    //неочень красиво мб переделать, а хотя норм
    @GetMapping("/answered/{id}")
    public List<Long> getAnswered(@PathVariable Long id){
        List<Expl> expls = explRepository.findAllByPara_Teacher_UserId(id);
        return expls.stream().map(el -> el.getPara().getId()).collect(Collectors.toList());
    }

    @PostMapping("/{paraId}/new")
    public ResponseEntity<Void> newExplanation(@RequestBody Map<String, String> json, @PathVariable Long paraId){
        Para para = paraRepository.findById(paraId).get();
        System.out.println(json.get("reason"));
        Expl res = new Expl(para, json.get("reason"), LocalDate.now());
        explRepository.save(res);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
