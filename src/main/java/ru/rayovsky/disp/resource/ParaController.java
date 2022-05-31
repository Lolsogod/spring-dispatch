package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.AuthenticationException;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.jwt.JwtUserDetails;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.repository.ParaRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/para/")
public class ParaController {
    private final ParaRepository paraRepository;

    public ParaController(ParaRepository paraRepository) {
        this.paraRepository = paraRepository;
    }

    @GetMapping("/")
    public List<List<Para>> getAllParas(){
        CheckAuthByRole();
        List<List<Para>> res = new ArrayList<>();
        IntStream.range(1,6).forEach(i->res.add(paraRepository.findAllByNumAndDate(i,LocalDate.now())));
        return res;
    }

    @PutMapping("/{id}/state/")
    public ResponseEntity<Void> setState(@PathVariable Long id, @RequestBody Map<String, String> json){
        CheckAuthByRole();
        if(json.get("state") ==null) throw new NothingFoundException("Праметр state не задан");
        try {
            Para para = paraRepository.findById(id).get();
            para.setState(json.get("state"));
            paraRepository.save(para);
        }catch (NoSuchElementException e){throw new NothingFoundException("Пара не найдена");}
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
    //код повторяется...попробывать переделать на анотации
    public void CheckAuthByRole(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof JwtUserDetails))
            throw  new AuthenticationException("Something wrong with your credentials");
        String role = ((JwtUserDetails)principal).getRole();
        if (!Arrays.asList("dispatcher", "decan", "admin").contains(role)) {
            throw new AuthorizationException("You dont have permission edit data");
        }

    }
}
