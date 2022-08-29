package edu.bju.todos.repos;

import edu.bju.todos.models.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByFusionAuthUserId(String fusionAuthUserId);
}
