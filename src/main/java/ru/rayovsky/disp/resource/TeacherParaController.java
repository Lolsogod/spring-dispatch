package ru.rayovsky.disp.resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.AuthenticationException;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.jwt.JwtUserDetails;
import ru.rayovsky.disp.model.Para;
import ru.rayovsky.disp.repository.ParaRepository;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/t-para/")
public class TeacherParaController {
    final ParaRepository paraRepository;
    public TeacherParaController(ParaRepository paraRepository) {
        this.paraRepository = paraRepository;
    }

    @GetMapping("/{id}")
    public List<Para> getUser(@PathVariable String id){
        CheckAuthById(Long.parseLong(id));
        List<Para> res = paraRepository.findAllByTeacher_UserIdOrderByDateAsc(Long.parseLong(id));
        if (res.size()==0) throw new NothingFoundException("Пары не найдены");
        return res;
    }
    @GetMapping("/count/{id}")
    public Integer countMisses(@PathVariable String id){
        CheckAuthById(Long.parseLong(id));
        return paraRepository.countAllByTeacher_UserIdAndStateEquals(Long.parseLong(id),"was-not");
    }
    //код повторяется...
    public void CheckAuthById(Long id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof JwtUserDetails))
            throw  new AuthenticationException("Something wrong with your credentials");
        Long authId = ((JwtUserDetails)principal).getId();
        String role = ((JwtUserDetails)principal).getRole();
        if (authId != id && !Arrays.asList("dispatcher", "decan", "admin").contains(role))
            throw  new AuthorizationException("You dont have permission to access other users data");
    }
}
