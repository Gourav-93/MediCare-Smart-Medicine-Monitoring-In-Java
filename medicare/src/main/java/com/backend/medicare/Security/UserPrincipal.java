package com.backend.medicare.Security;

import com.backend.medicare.Model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Integer id;
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Integer id, String name, String email, String password, String role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String normalizedRole = user.getRole().toUpperCase().trim();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + normalizedRole));
        authorities.add(new SimpleGrantedAuthority(normalizedRole));

        return new UserPrincipal(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                normalizedRole,
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
