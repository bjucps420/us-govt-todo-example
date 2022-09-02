package edu.bju.todos.controllers;

import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.EmailChangeDto;
import edu.bju.todos.dtos.PasswordChangeDto;
import edu.bju.todos.dtos.SecurityConfigUserDto;
import edu.bju.todos.dtos.TwoFactorDto;
import edu.bju.todos.dtos.UserWrapperDto;
import edu.bju.todos.services.FusionAuthService;
import edu.bju.todos.utils.ApiResponse;
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

    @GetMapping(path = "/current", produces = "application/json")
    @PreAuthorize("permitAll()")
    public UserWrapperDto current() {
        if(security.getUser() != null) {
            var user = fusionAuthService.findByFusionAuthUserId(security.getUser().getFusionAuthUserId());
            if (user.isPresent()) {
                return new UserWrapperDto(
                    new SecurityConfigUserDto(
                        security
                    )
                );
            }
        }
        return null;
    }

    @GetMapping(value = "/get-secret", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<SecretResponse> getSecret() {
        return ApiResponse.success(fusionAuthService.generateSecret());
    }

    @PostMapping(value = "/toggle-two-factor", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TwoFactorResponse> toggleTwoFactor(@RequestBody TwoFactorDto twoFactorDto) {
        return ApiResponse.success(fusionAuthService.toggleTwoFactor(security.getUser().getFusionAuthUserId(), twoFactorDto));
    }

    @PostMapping(value = "/change-email", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Boolean> changeEmail(@RequestBody EmailChangeDto emailChangeDto) {
        fusionAuthService.updateEmail(security.getUser().getFusionAuthUserId(), emailChangeDto.getNewEmail());
        security.getUser().setEmail(emailChangeDto.getNewEmail());
        return ApiResponse.success(true);
    }

    @PostMapping(value = "/change-password", consumes = "application/json", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Boolean> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        if(fusionAuthService.checkPassword(security.getUser().getEmail(), passwordChangeDto.getCurrentPassword()).getFirst() < 300) {
            fusionAuthService.updatePassword(security.getUser().getFusionAuthUserId(), passwordChangeDto.getNewPassword(), false);
        }
        return ApiResponse.success(true);
    }
}
