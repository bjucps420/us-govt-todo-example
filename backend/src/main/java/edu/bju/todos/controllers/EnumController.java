package edu.bju.todos.controllers;

import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/enum/")
public class EnumController {
    public static final String CACHE_PREFIX = "enum-controller-";

    @Cacheable(CACHE_PREFIX + "status")
    @GetMapping(path = "status/all", produces = "application/json")
    @PreAuthorize("permitAll()")
    public Status[] allStatus() {
        return Status.values();
    }

    @Cacheable(CACHE_PREFIX + "type")
    @GetMapping(path = "type/all", produces = "application/json")
    @PreAuthorize("permitAll()")
    public Type[] allType() {
        return Type.values();
    }
}
