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
    public void checkRoleAndId(Long id, String role){
        JwtUserDetails principal = checkedPrincipal();
        if(!roleCheck(principal,"admin") && (!idCheck(principal, id) || !roleCheck(principal,role)))
            throw  new AuthorizationException("У вас нет доступа к данным других пользователей");
    }
    public void checkAdmin(){
        JwtUserDetails principal = checkedPrincipal();
        if(!roleCheck(principal,"admin"))
            throw  new AuthorizationException("Вы не админ");
    }
    //разрешено хозяину и страшим ролям
    public void checkUpperAndId(Long id){
        JwtUserDetails principal = checkedPrincipal();
        if(!upperRoleCheck(principal) && !idCheck(principal, id))
            throw  new AuthorizationException("У вас нет доступа к данным других пользователей");
    }
    //разрешено только старшим ролям
    public void checkUpper(){
        JwtUserDetails principal = checkedPrincipal();
        if(!upperRoleCheck(principal))
            throw new AuthorizationException("У вас нет доступа к редактированию данных");
    }

    public boolean isTeacher(){
        JwtUserDetails principal = checkedPrincipal();
        return  roleCheck(principal, "teacher");
    }
    private boolean idCheck(JwtUserDetails principal, Long id){
        Long authId = principal.getId();
        return Objects.equals(authId, id);
    }
    private boolean upperRoleCheck(JwtUserDetails principal){
        String role = principal.getRole();
        return Arrays.asList("dispatcher", "decan", "admin").contains(role);
    }
    private boolean roleCheck(JwtUserDetails principal, String chRole){
        String role = principal.getRole();
        return role.equals(chRole);
    }

    private JwtUserDetails checkedPrincipal(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof JwtUserDetails))
            throw  new AuthenticationException("Некоректные данные авторизации");
        else return (JwtUserDetails)principal;
    }
}
