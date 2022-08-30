package edu.bju.todos.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Pair<M, N> implements Serializable {
    @With private M first;
    @With private N second;
}
