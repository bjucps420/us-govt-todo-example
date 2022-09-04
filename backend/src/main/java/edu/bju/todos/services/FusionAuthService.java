package edu.bju.todos.services;

import edu.bju.todos.dtos.TwoFactorDto;
import edu.bju.todos.utils.Pair;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.ChangePasswordReason;
import io.fusionauth.domain.User;
import io.fusionauth.domain.UserRegistration;
import io.fusionauth.domain.api.LoginRequest;
import io.fusionauth.domain.api.LoginResponse;
import io.fusionauth.domain.api.TwoFactorRequest;
import io.fusionauth.domain.api.TwoFactorResponse;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.twoFactor.SecretResponse;
import io.fusionauth.domain.api.twoFactor.TwoFactorLoginRequest;
import io.fusionauth.domain.api.user.ForgotPasswordRequest;
import io.fusionauth.domain.api.user.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FusionAuthService {
    private final FusionAuthClient fusionAuthClient;

    @Value("${fusionAuth.applicationId}")
    private String clientId;
    @Value("${fusionAuth.apiKey}")
    private String apiKey;
    @Value("${link-url}")
    private String baseUrl;

    public User createUser(String email, String name, String initialPassword) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.user = new User();
        registrationRequest.user.id = UUID.randomUUID();
        registrationRequest.user.email = email;
        registrationRequest.user.password = initialPassword;
        registrationRequest.sendSetPasswordEmail = false;
        registrationRequest.skipVerification = true;
        registrationRequest.user.fullName = name;
        registrationRequest.registration = new UserRegistration();
        registrationRequest.registration.applicationId = UUID.fromString(clientId);
        registrationRequest.skipRegistrationVerification = true;
        var result = fusionAuthClient.register(registrationRequest.user.id, registrationRequest);
        if(result.wasSuccessful()) {
            return result.successResponse.user;
        }
        return null;
    }

    public Optional<User> findByEmail(String email) {
        var result = fusionAuthClient.retrieveUserByEmail(email);
        if(result.wasSuccessful()) {
            return Optional.of(result.successResponse.user);
        }
        return Optional.empty();
    }

    public Optional<User> findByFusionAuthUserId(String fusionAuthUserId) {
        var result = fusionAuthClient.retrieveUser(UUID.fromString(fusionAuthUserId));
        if(result.wasSuccessful()) {
            return Optional.ofNullable(result.successResponse.user);
        }
        return Optional.empty();
    }

    public SecretResponse generateSecret() {
        var response = fusionAuthClient.generateTwoFactorSecret();
        return response.wasSuccessful() ? response.successResponse : null;
    }

    public TwoFactorResponse toggleTwoFactor(String fusionAuthUserId, TwoFactorDto twoFactorDto) {
        final String AUTHENTICATOR = "authenticator";
        var user = findByFusionAuthUserId(fusionAuthUserId);
        if (user.isPresent()) {
            if (twoFactorDto.isEnableTwoFactor()) {
                var request = new TwoFactorRequest(twoFactorDto.getCode(), AUTHENTICATOR, twoFactorDto.getSecret());
                var response = fusionAuthClient.enableTwoFactor(user.get().id, request);
                if (response.wasSuccessful()) {
                    return response.successResponse;
                }
            } else {
                var response = fusionAuthClient.disableTwoFactor(user.get().id, user.get().twoFactor.methods.get(0).id, twoFactorDto.getCode());
                return response.wasSuccessful() ? new TwoFactorResponse() : null;
            }
        }
        return null;
    }

    public Pair<Integer, LoginResponse> checkPassword(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.loginId = email;
        request.password = password;
        var result = fusionAuthClient.login(request);
        if(result.wasSuccessful()) {
            return new Pair<>(result.status, result.successResponse);
        }
        return new Pair<>(result.status, null);
    }

    public User getUserByLogin(String username) {
        var result = fusionAuthClient.retrieveUserByLoginId(username);
        if(result.wasSuccessful()) {
            return result.successResponse.user;
        }
        return null;
    }

    public boolean updatePassword(String fusionAuthUserId, String password, boolean requireChange) {
        var user = findByFusionAuthUserId(fusionAuthUserId);
        if(user.isPresent()) {
            return updatePassword(user.get(), password, requireChange);
        }
        return false;
    }

    public boolean updatePassword(User user, String password, boolean requireChange) {
        UserRequest req = new UserRequest();
        req.user = user;
        req.user.password = password;
        req.user.passwordChangeReason = ChangePasswordReason.Administrative;
        req.user.passwordChangeRequired = requireChange;
        return fusionAuthClient.updateUser(user.id, req).wasSuccessful();
    }

    public boolean completeTwoFactorLogin(String twoFactorId, String twoFactorCode) {
        TwoFactorLoginRequest req = new TwoFactorLoginRequest();
        req.twoFactorId = twoFactorId;
        req.code = twoFactorCode;
        return fusionAuthClient.twoFactorLogin(req).wasSuccessful();
    }

    public boolean startForgotPassword(String username) throws IOException {
        var user = getUserByLogin(username);
        if(user != null) {
            var forgotPassword = new ForgotPasswordRequest();
            forgotPassword.sendForgotPasswordEmail = false;
            forgotPassword.applicationId = UUID.fromString(clientId);
            forgotPassword.loginId = username;
            var response = fusionAuthClient.forgotPassword(forgotPassword);
            return response.wasSuccessful();
        }
        return false;
    }

    public User updatePassword(String passwordResetToken, String password) {
        var response = fusionAuthClient.retrieveUserByChangePasswordId(passwordResetToken);
        if(response.wasSuccessful()) {
            updatePassword(response.successResponse.user, password, false);
            return response.successResponse.user;
        }
        return null;
    }

    public void updateEmail(String fusionAuthUserId, String email) {
        var user = findByFusionAuthUserId(fusionAuthUserId);
        if (user.isPresent()) {
            user.get().email = email;
            fusionAuthClient.updateUser(user.get().id, new UserRequest(user.get()));
        }
    }
}

