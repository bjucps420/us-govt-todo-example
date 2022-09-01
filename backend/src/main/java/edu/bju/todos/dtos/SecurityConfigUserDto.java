package edu.bju.todos.dtos;

import edu.bju.todos.config.SecurityConfig;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SecurityConfigUserDto {
    private String name;
    private String username;
    private String email;
    private String fusionAuthUserId;
    private Set<String> roles = new HashSet<>();
    private boolean authenticated;
    private boolean twoFactorEnabled;

    public SecurityConfigUserDto(SecurityConfig.Security security) {
        var user = security.getUser();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fusionAuthUserId = user.getFusionAuthUserId();
        this.roles = user.getRoles();
        this.authenticated = user.isAuthenticated();
        this.twoFactorEnabled = user.isTwoFactorEnabled();
    }
}
