package edu.bju.todos.dtos;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String newPassword;
    private String twoFactorCode;
    private String forgotPasswordCode;
    private Boolean success;
    private Boolean requiresPasswordChange;
    private Boolean requiresTwoFactorCode;
}
