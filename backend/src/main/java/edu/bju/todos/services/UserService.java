package edu.bju.todos.services;

import edu.bju.todos.models.User;
import edu.bju.todos.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByFusionAuthUserId(String fusionAuthUserId) {
        return userRepository.findByFusionAuthUserId(fusionAuthUserId);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User save(User user) {
        if (user.getId() == null || user.getId() == 0L) {
            user.setId(null);
        }
        userRepository.save(user);
        return user;
    }
}
