package com.marioborrego.api.calculodeduccionesbackend.security.infrastructure;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetails para Spring Security
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Añade roles como authorities con prefijo ROLE_
        var roleAuthorities = user.getRoles().stream()
                .filter(role -> role.getActive())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        // Añade permisos como authorities
        var permissionAuthorities = user.getAllPermissions().stream()
                .filter(permission -> permission.getActive())
                .map(permission -> new SimpleGrantedAuthority(permission.getName().name()))
                .collect(Collectors.toList());

        roleAuthorities.addAll(permissionAuthorities);
        return roleAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }

    public User getUser() {
        return user;
    }
}
