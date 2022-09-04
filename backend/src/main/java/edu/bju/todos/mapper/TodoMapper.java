package edu.bju.todos.mapper;

import edu.bju.todos.dtos.TodoDto;
import edu.bju.todos.models.Todo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
    componentModel = "spring"
)
public interface TodoMapper {
    default Todo toTodo(Long id) {
        return Todo.of(id);
    }

    default Long fromTodo(Todo todo) {
        return todo != null ? todo.getId() : null;
    }

    TodoDto from(Todo todo);

    Todo to(TodoDto todoDto);

    List<TodoDto> from(List<Todo> todos);

    List<Todo> to(List<TodoDto> todoDtos);
}
