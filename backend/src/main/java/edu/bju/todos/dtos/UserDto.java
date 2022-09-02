package edu.bju.todos.dtos;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String fusionAuthUserId;
}
