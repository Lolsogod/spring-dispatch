package ru.rayovsky.disp.resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.UserRepository;
import ru.rayovsky.disp.service.AuthCheckService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/decan/")
public class DecanController {
    final private UserRepository userRepository;
    private final AuthCheckService authCheckService;

    public DecanController(UserRepository userRepository, AuthCheckService authCheckService) {
        this.userRepository = userRepository;
        this.authCheckService = authCheckService;
    }

    @GetMapping(value = "/")
    public List<User> getAllTeachers(){
        authCheckService.CheckUpper();
        return userRepository.findAllByRoleEquals("teacher");
    }
}
