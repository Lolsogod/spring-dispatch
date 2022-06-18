package ru.rayovsky.disp.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.NothingFoundException;
import ru.rayovsky.disp.model.ParaPrototype;
import ru.rayovsky.disp.model.Subject;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.ParaPrototypeRepository;
import ru.rayovsky.disp.repository.SubjectRepository;
import ru.rayovsky.disp.repository.UserRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final AuthCheckService authCheckService;
    private final UserRepository userRepository;
    private final ParaPrototypeRepository paraPrototypeRepository;
    private final SubjectRepository subjectRepository;

    public AdminController(AuthCheckService authCheckService, UserRepository userRepository, ParaPrototypeRepository paraPrototypeRepository, SubjectRepository subjectRepository) {
        this.authCheckService = authCheckService;
        this.userRepository = userRepository;
        this.paraPrototypeRepository = paraPrototypeRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping(value = "/")
    public List<User> getAllUsers(){
        authCheckService.checkAdmin();
        return userRepository.findAll();
    }

    @PutMapping(value = "/{id}/role")
    public ResponseEntity<HttpStatus> setRole(@PathVariable Long id, @RequestBody Map<String, String> json){
        authCheckService.checkAdmin();
        User user = userRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Пользователь не найден"));
        user.setRole(json.get("curRole"));
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping(value = "/{id}/rasp/")
    public List<List<ParaPrototype>> getRasp(@PathVariable Long id){
        authCheckService.checkRoleAndId(id, "teacher");
        List<List<ParaPrototype>> res = new ArrayList<>();
        IntStream.range(1,7)
                .forEach(i->res.add(paraPrototypeRepository.findAllByTeacher_UserIdAndActiveIsTrueAndNum(id, i)));
        return res;
    }

    @PutMapping(value = "/{id}/prototype/")
    public ResponseEntity<HttpStatus> editRasp(@PathVariable Long id,  @RequestBody Map<String, String> json){
        authCheckService.checkAdmin();

        ParaPrototype proto = paraPrototypeRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Прототип не найден"));
        proto.setAudit(json.get("audit"));
        proto.setType(json.get("type"));
        Subject sub = subjectRepository.findById(Long.parseLong(json.get("subjectId")))
                .orElseThrow(()->new NothingFoundException("Предмет не найден"));
        proto.setSubject(sub);
        paraPrototypeRepository.save(proto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/new-prototype/")
    public Long addRasp(@RequestBody Map<String, String> json) {
        authCheckService.checkAdmin();
        User teacher = userRepository.findById(Long.parseLong(json.get("userId")))
                .orElseThrow(() -> new NothingFoundException("Учитель не найден"));
        Subject sub = subjectRepository.findById(Long.parseLong(json.get("subjectId")))
                .orElseThrow(() -> new NothingFoundException("Предмет не найден"));
        String type = json.get("type");
        String audit = json.get("audit");
        DayOfWeek day = DayOfWeek.valueOf(json.get("day"));
        int num = Integer.parseInt(json.get("num"));

        ParaPrototype proto = new ParaPrototype(teacher, sub, type, audit, day, num, true);
        paraPrototypeRepository.save(proto);
        paraPrototypeRepository.flush();
        return proto.getId();
    }
    @PutMapping(value = "/{id}/disable/")
    public ResponseEntity<HttpStatus> disableRasp(@PathVariable Long id){
        authCheckService.checkAdmin();

        ParaPrototype proto = paraPrototypeRepository.findById(id)
                .orElseThrow(()->new NothingFoundException("Прототип не найден"));
        proto.setActive(false);
        paraPrototypeRepository.save(proto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/subs")
    public List<Subject> getSubs(){
        authCheckService.checkAdmin();

        return subjectRepository.findAll();
    }
}
