package edu.bju.todos.mapper;

import edu.bju.todos.dtos.UserDto;
import edu.bju.todos.models.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
    componentModel = "spring"
)
public interface UserMapper {
    default User toUser(Long id) {
        return User.of(id);
    }

    default Long fromUser(User todo) {
        return todo != null ? todo.getId() : null;
    }

    UserDto from(User user);

    User to(UserDto userDto);

    List<UserDto> from(List<User> users);

    List<User> to(List<UserDto> userDtos);
}
