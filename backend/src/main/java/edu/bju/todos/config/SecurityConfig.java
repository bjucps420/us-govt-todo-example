package edu.bju.todos.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.bju.todos.enums.Role;
import edu.bju.todos.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/api/auth/**", "/actuator/**", "/error").permitAll()
            .and().cors().and().csrf().disable();
    }

    @AllArgsConstructor
    @Data
    public static class UserAuthority implements GrantedAuthority {
        private String authority;

        @Override
        public String getAuthority() {
            return authority;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User implements Authentication {
        private String name;
        private String email;
        private String fusionAuthUserId;
        private Set<String> roles = new HashSet<>();
        private boolean authenticated;
        private boolean twoFactorEnabled;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream().map(UserAuthority::new).collect(Collectors.toSet());
        }

        @Override
        @JsonIgnore
        public Object getCredentials() {
            return this;
        }

        @Override
        @JsonIgnore
        public Object getDetails() {
            return this;
        }

        @Override
        @JsonIgnore
        public Object getPrincipal() {
            return this;
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {
            this.authenticated = b;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Security implements Serializable {
        public SecurityConfig.User getUser() {
            try {
                return (SecurityConfig.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            } catch (ClassCastException e) {
                // this is okay, it means we are not logged in
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

        public boolean hasAid() {
            if(getUser() == null) return false;
            return getUser().getRoles().contains(Role.AID.getDescription());
        }

        public boolean hasClassified() {
            if(getUser() == null) return false;
            return getUser().getRoles().contains(Role.CLASSIFIED.getDescription());
        }

        public boolean hasSecret() {
            if(getUser() == null) return false;
            return getUser().getRoles().contains(Role.SECRET.getDescription());
        }

        public boolean hasTopSecret() {
            if(getUser() == null) return false;
            return getUser().getRoles().contains(Role.TOP_SECRET.getDescription());
        }

        public boolean hasUnclassified() {
            if(getUser() == null) return false;
            return getUser().getRoles().contains(Role.UNCLASSIFIED.getDescription());
        }

        public List<Type> getTypes() {
            List<Type> types = new ArrayList<>();
            if(hasAid() || hasTopSecret()) {
                types.add(Type.TOP_SECRET);
            }
            if(hasAid() || hasSecret()) {
                types.add(Type.SECRET);
            }
            if(hasAid() || hasClassified()) {
                types.add(Type.CLASSIFIED);
            }
            types.add(Type.UNCLASSIFIED); // everyone gets these types
            return types;
        }
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION,
           proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Security security() {
        return new Security();
    }
}
