package edu.bju.todos.controllers;

import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.EmailChangeDto;
import edu.bju.todos.dtos.PasswordChangeDto;
import edu.bju.todos.dtos.TwoFactorDto;
import edu.bju.todos.services.FusionAuthService;
import io.fusionauth.domain.api.TwoFactorResponse;
import io.fusionauth.domain.api.twoFactor.SecretResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/")
public class UserController {
    private final FusionAuthService fusionAuthService;
    private final SecurityConfig.Security security;

    @GetMapping(value = "/get-secret", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public SecretResponse getSecret() {
        return fusionAuthService.generateSecret();
    }

    @PostMapping(value = "/toggle-two-factor", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public TwoFactorResponse toggleTwoFactor(@RequestBody TwoFactorDto twoFactorDto) {
        return fusionAuthService.toggleTwoFactor(security.getUser().getFusionAuthUserId(), twoFactorDto);
    }

    @PostMapping(value = "/change-email", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public void changeEmail(@RequestBody EmailChangeDto emailChangeDto) {
        fusionAuthService.updateEmail(security.getUser().getFusionAuthUserId(), emailChangeDto.getNewEmail());
    }

    @PostMapping(value = "/change-password", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public void changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        if(fusionAuthService.checkPassword(security.getUser().getEmail(), passwordChangeDto.getCurrentPassword()).getFirst() < 300) {
            fusionAuthService.updatePassword(security.getUser().getFusionAuthUserId(), passwordChangeDto.getNewPassword(), false);
        }
    }
}
