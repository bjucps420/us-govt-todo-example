package edu.bju.todos.controllers;

import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.LoginDto;
import edu.bju.todos.services.FusionAuthService;
import io.fusionauth.domain.User;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    private final FusionAuthService fusionAuthService;

    private final int PASSWORD_CHANGE_REQUIRED = 203;
    private final int TWO_FACTOR_CODE_REQUIRED = 242;

    @Value("${fusionAuth.applicationId}")
    private String clientId;

    @PostMapping(value = "/register")
    @PreAuthorize("permitAll()")
    public void register(HttpServletRequest req, @RequestBody LoginDto loginDto) {

    }

    @PostMapping(value = "/login")
    @PreAuthorize("permitAll()")
    public LoginDto login(HttpServletRequest req, @RequestBody LoginDto loginDto) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        var response = fusionAuthService.checkPassword(loginDto.getUsername(), loginDto.getPassword());
        User user = null;
        if(response != null && response.getSecond() != null) {
            user = response.getSecond().user;
        }
        if (StringUtils.isNotBlank(loginDto.getForgotPasswordCode())) {
            user = fusionAuthService.updatePassword(loginDto.getForgotPasswordCode(), loginDto.getNewPassword());
            if (user == null) {
                loginDto.setSuccess(false);
                return loginDto;
            }
        } else if (response == null) {
            loginDto.setSuccess(false);
            return loginDto;
        } else if (response.getFirst() == TWO_FACTOR_CODE_REQUIRED && StringUtils.isBlank(loginDto.getTwoFactorCode())) {
            loginDto.setSuccess(true);
            loginDto.setRequiresTwoFactorCode(true);
            return loginDto;
        } else if (response.getFirst() == TWO_FACTOR_CODE_REQUIRED) {
            user = fusionAuthService.getUserByLogin(loginDto.getUsername());
            if(!fusionAuthService.completeTwoFactorLogin(response.getSecond().twoFactorId, loginDto.getTwoFactorCode())) {
                loginDto.setSuccess(false);
                return loginDto;
            }
        } else if (response.getFirst() == PASSWORD_CHANGE_REQUIRED && StringUtils.isBlank(loginDto.getNewPassword())) {
            loginDto.setSuccess(true);
            loginDto.setRequiresPasswordChange(true);
            return loginDto;
        } else if (response.getFirst() == PASSWORD_CHANGE_REQUIRED) {
            user = fusionAuthService.getUserByLogin(loginDto.getUsername());
            fusionAuthService.updatePassword(user, loginDto.getNewPassword(), false);
        }

        var securityUser = new SecurityConfig.User(user.fullName, user.email, user.id.toString(), user.getRoleNamesForApplication(UUID.fromString(clientId)), true, user.twoFactorEnabled());
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(securityUser);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        var resp = new LoginDto();
        resp.setSuccess(true);
        return resp;
    }

    @GetMapping(value = "/logout")
    @PreAuthorize("permitAll()")
    public void logout(HttpServletRequest req) {
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(null);
        HttpSession session = req.getSession(true);
        session.removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }
}
