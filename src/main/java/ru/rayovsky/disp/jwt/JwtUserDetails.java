package ru.rayovsky.disp.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.rayovsky.disp.model.User;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 5155720064139820502L;

    private final Long id;
    private final String username;
    private final String password;

    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(User user){
        this(user.getUserId(), user.getEmail(), user.getPassword(), user.getRole());
    }
    public JwtUserDetails(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        //позже разобратся
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        //простая роль(наверное костыль)
        this.role =role;

        this.authorities = authorities;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public String getRole() {
        return role;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

