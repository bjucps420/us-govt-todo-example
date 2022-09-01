package edu.bju.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWrapperDto {
    private SecurityConfigUserDto user;
}
