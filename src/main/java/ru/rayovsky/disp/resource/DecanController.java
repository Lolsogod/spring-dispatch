package ru.rayovsky.disp.resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.UserRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/api/decan/")
public class DecanController {
    final UserRepository userRepository;

    public DecanController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/")
    public List<User> getAllTeachers(){
        return userRepository.findAllByRoleEquals("teacher");
    }
}
