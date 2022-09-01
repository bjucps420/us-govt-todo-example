package edu.bju.todos.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponse<T> {
    private Long total;
    private List<T> items;
}
