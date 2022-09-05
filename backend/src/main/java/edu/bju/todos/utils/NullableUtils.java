package edu.bju.todos.utils;

import java.util.HashSet;
import java.util.Set;

public class NullableUtils {
    public static <T> Set<T> nonNull(Set<T> set) {
        return set == null ? new HashSet<>() : set;
    }
}
