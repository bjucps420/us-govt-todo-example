package edu.bju.todos.services;

import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import edu.bju.todos.models.Todo;
import edu.bju.todos.repos.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public Optional<Todo> findById(Long id, List<Type> types) {
        return todoRepository.findByIdAndTypeIn(id, types);
    }

    public Page<Todo> findAllByTitleLike(String title, List<Type> types, List<Status> statues, Pageable pageable) {
        return todoRepository.findAllByTitleContainsAndTypeInAndStatusIn(title, types, statues, pageable);
    }
    public Page<Todo> findAll(List<Type> types, List<Status> statues, Pageable pageable) {
        return todoRepository.findAllByTypeInAndStatusIn(types, statues, pageable);
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null || todo.getId() == 0L) {
            todo.setId(null);
        }
        todoRepository.save(todo);
        return todo;
    }

    public boolean delete(Todo todo) {
        todoRepository.delete(todo);
        return true;
    }

    public boolean deleteAll() {
        todoRepository.deleteAll();
        return true;
    }
}
