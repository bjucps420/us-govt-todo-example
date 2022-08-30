package edu.bju.todos.dtos;

import lombok.Data;

@Data
public class TwoFactorDto {
    private String secret;
    private String code;
    private String secretBase32;
    private boolean enableTwoFactor;
}
