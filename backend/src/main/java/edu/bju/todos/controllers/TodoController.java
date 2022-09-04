package edu.bju.todos.controllers;

import edu.bju.todos.config.SecurityConfig;
import edu.bju.todos.dtos.TodoDto;
import edu.bju.todos.enums.Status;
import edu.bju.todos.mapper.TodoMapper;
import edu.bju.todos.models.Todo;
import edu.bju.todos.services.TodoService;
import edu.bju.todos.utils.ApiResponse;
import edu.bju.todos.utils.SearchResponse;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/todo/")
public class TodoController {
    private final TodoService todoService;
    private final TodoMapper todoMapper;
    private final SecurityConfig.Security security;

    @GetMapping(path = "list/{status}", produces = "application/json")
    @PreAuthorize("permitAll()")
    public ApiResponse<SearchResponse<TodoDto>> list(
        @RequestParam(value = "search", required = false, defaultValue = "") String search,
        @RequestParam(value = "groupBy", required = false, defaultValue = "") String groupByParam,
        @RequestParam(value = "groupDesc", required = false, defaultValue = "") String groupDescParam,
        @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortByParam,
        @RequestParam(value = "sortDesc", required = false, defaultValue = "false") String sortDescParam,
        @RequestParam(value = "page", required = false, defaultValue = "1") String pageParam,
        @RequestParam(value = "mustSort", required = false, defaultValue = "false") String mustSortParam,
        @RequestParam(value = "multiSort", required = false, defaultValue = "false") String multiSortParam,
        @RequestParam(value = "itemsPerPage", required = false, defaultValue = "10") String itemsPerPageParam,
        @PathVariable String status
    ) {
        String[] groupBy = groupByParam.split(",");
        String[] groupByDesc = groupDescParam.split(",");
        String[] sortBy = sortByParam.split(",");
        String[] sortByDesc = sortDescParam.split(",");
        Integer page = Integer.parseInt(pageParam);
        Integer itemsPerPage = Integer.parseInt(itemsPerPageParam);
        Boolean mustSort = Boolean.parseBoolean(mustSortParam);
        Boolean multiSort = Boolean.parseBoolean(multiSortParam);

        var pageRequest = PageRequest.of(page - 1, itemsPerPage);
        if(sortBy.length > 0) {
            if(sortByDesc[0].equals("true")) {
                pageRequest = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.DESC, sortBy[0]);
            } else {
                pageRequest = PageRequest.of(page - 1, itemsPerPage, Sort.Direction.ASC, sortBy[0]);
            }
        }

        Status statusToList = Status.valueOf(status);
        Page<Todo> dataPage = null;
        if(StringUtils.isBlank(search)) {
            dataPage = todoService.findAll(
                security.getTypes(), List.of(statusToList), pageRequest
            );
        } else {
            dataPage = todoService.findAllByTitleLike(
                search, security.getTypes(), List.of(statusToList), pageRequest
            );
        }

        return ApiResponse.success(new SearchResponse<>(dataPage.getTotalElements(), todoMapper.from(dataPage.toList())));
    }

    @GetMapping(path = "/{id:\\d+}", produces = "application/json")
    @PreAuthorize("permitAll()")
    public ApiResponse<TodoDto> get(@PathVariable Long id) {
        var todo = todoService.findById(id, security.getTypes());
        return todo.map(value -> ApiResponse.success(todoMapper.from(value))).orElseGet(() -> ApiResponse.error("todo not found"));
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @PreAuthorize("!@security.hasAid()")
    public ApiResponse<TodoDto> create(@RequestBody TodoDto todoDto) {
        if(todoDto.getId() == null || todoDto.getId() == 0L) {
            var todo = todoMapper.to(todoDto);
            List<String> validationErrors = validate(todo);
            if(validationErrors.isEmpty()) {
                if (security.getUser() != null) {
                    todo.setCreatedBy(security.getUser().getEmail());
                }
                return ApiResponse.success(todoMapper.from(todoService.save(todo)));
            }
            return ApiResponse.error("validation error(s): " + validationErrors.stream().collect(Collectors.joining(", ")));
        }
        return ApiResponse.error("cannot update todo via create");
    }

    private List<String> validate(Todo todo) {
        List<String> errors = new ArrayList<>();
        if(todo.getTitle().length() > 255) errors.add("title is too long (max 255 characters)");
        if(todo.getDescription().length() > 2048) errors.add("title is too long (max 2048 characters)");
        if(!security.getTypes().contains(todo.getType())) errors.add("you are not permitted to create a todo of that classification");
        if(!security.hasAid() && todo.getStatus() != Status.PENDING) errors.add("you are not permitted to change the status of a todo");
        return errors;
    }

    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@security.hasAid()")
    public ApiResponse<TodoDto> update(@RequestBody TodoDto todoDto) {
        var todo = todoMapper.to(todoDto);
        List<String> validationErrors = validate(todo);
        if(validationErrors.isEmpty()) {
            if (security.getUser() != null) {
                todo.setUpdatedBy(security.getUser().getEmail());
            }
            return ApiResponse.success(todoMapper.from(todoService.save(todo)));
        }
        return ApiResponse.error("validation error(s): " + validationErrors.stream().collect(Collectors.joining(", ")));
    }

    @PostMapping(path = "/delete", consumes = "application/json", produces = "application/json")
    @PreAuthorize("@security.hasAid()")
    public ApiResponse<Boolean> delete(@RequestBody TodoDto todoDto) {
        var todo = todoMapper.to(todoDto);
        return ApiResponse.success(todoService.delete(todo));
    }
}
