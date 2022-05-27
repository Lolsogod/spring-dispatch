package ru.rayovsky.disp.jwt.resource;

import ru.rayovsky.disp.exception.AuthorizationException;

import java.io.Serializable;

public class  JwtTokenRequest implements Serializable {

    private static final long serialVersionUID = -5616176897013108345L;

    private String username;
    private String password;

    public JwtTokenRequest() {
        super();
    }

    public JwtTokenRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        nullCheck(username);
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        nullCheck(password);
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    private void nullCheck(Object obj){
        if(obj == null) throw new AuthorizationException("не задан логин или пароль");
    }
}
