import { AuthService } from './auth.service'
import { EnumService } from './enum.service'
import { TodoService } from './todo.service'
import { UserService } from './user.service'


export default ({ app: { $axios } }, inject) => {
  const authService = new AuthService($axios);
  const enumService = new EnumService($axios);
  const todoService = new TodoService($axios);
  const userService = new UserService($axios);

  inject('authService', authService);
  inject('enumService', enumService);
  inject('todoService', todoService);
  inject('userService', userService);
}
