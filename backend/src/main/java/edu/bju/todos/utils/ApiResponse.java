package edu.bju.todos.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String errorMessage;
    private T response;

    public static <T> ApiResponse<T> error(String errorMessage) {
        return new ApiResponse<>(false, errorMessage, null);
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(true, null, response);
    }
}
