package ru.rayovsky.disp.jwt.resource;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import ru.rayovsky.disp.exception.AuthenticationException;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.jwt.JwtTokenUtil;
import ru.rayovsky.disp.jwt.JwtUserDetails;
import ru.rayovsky.disp.model.User;
import ru.rayovsky.disp.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins="http://localhost:3000")
public class JwtAuthenticationRestController {

    @Value("${jwt.http.request.header}")
    private String tokenHeader;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService jwtUserDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationRestController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserDetailsService jwtUserDetailsService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.userRepository = userRepository;
    }
    //login
    @PostMapping(value = "${jwt.get.token.uri}")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest)
            throws AuthenticationException {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final JwtUserDetails userDetails = (JwtUserDetails) jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }
    //refresh(не использую)
    @GetMapping(value = "${jwt.refresh.token.uri}")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUserDetails user = (JwtUserDetails) jwtUserDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token)) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> resisterNewUser(@RequestBody User user){
        //доделать валидацию
        if(userRepository.findByEmail(user.getEmail()).isEmpty() &&
            checkEmail(user.getEmail()) && user.getPassword().length()>=6){
            user.encryptPassword();
            userRepository.save(user);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        throw new AuthorizationException("Не корректные данные");
    }
    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthorizationException("Пользователь заблокирован");
        } catch (BadCredentialsException e) {
            throw new AuthorizationException("Неправильный логин или пароль");
        }
    }

    private boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z\\d._%+-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }
}
