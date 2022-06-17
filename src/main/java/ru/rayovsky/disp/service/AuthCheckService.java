package ru.rayovsky.disp.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.rayovsky.disp.exception.AuthenticationException;
import ru.rayovsky.disp.exception.AuthorizationException;
import ru.rayovsky.disp.jwt.JwtUserDetails;

import java.util.Arrays;
import java.util.Objects;

@Service
public class AuthCheckService {
    //разрешено хозяину и страшим ролям
    public void CheckAuthById(Long id){
        JwtUserDetails principal = checkedPrincipal();
        if(!RoleCheck(principal) && !IdCheck(principal, id))
            throw  new AuthorizationException("У вас нет доступа к данным других пользователей");
    }
    //разрешено только старшим ролям
    public void CheckAuthByRole(){
        JwtUserDetails principal = checkedPrincipal();
        if(!RoleCheck(principal))
            throw new AuthorizationException("У вас нет доступа к редактированию данных");
    }
    public boolean IdCheck(JwtUserDetails principal, Long id){
        Long authId = principal.getId();
        return Objects.equals(authId, id);
    }
    public boolean RoleCheck(JwtUserDetails principal){
        String role = principal.getRole();
        return Arrays.asList("dispatcher", "decan", "admin").contains(role);
    }

    private JwtUserDetails checkedPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof JwtUserDetails))
            throw  new AuthenticationException("Некоректные данные авторизации");
        else return (JwtUserDetails)principal;
    }
}
