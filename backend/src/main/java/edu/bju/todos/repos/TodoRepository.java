package edu.bju.todos.repos;

import edu.bju.todos.enums.Status;
import edu.bju.todos.enums.Type;
import edu.bju.todos.models.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends PagingAndSortingRepository<Todo, Long> {
    Page<Todo> findAllByTypeInAndStatusIn(List<Type> types, List<Status> statues, Pageable pageable);

    Optional<Todo> findByIdAndTypeIn(Long id, List<Type> types);
}
