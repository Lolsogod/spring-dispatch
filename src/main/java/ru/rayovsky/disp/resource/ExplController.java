package ru.rayovsky.disp.resource;

import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.model.Expl;
import ru.rayovsky.disp.repository.ExplRepository;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/expl/")
public class ExplController {

    private final ExplRepository explRepository;

    public ExplController(ExplRepository explRepository) {
        this.explRepository = explRepository;
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
}