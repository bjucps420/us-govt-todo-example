import { AuthService } from './auth.service'
import { UserService } from './user.service'


export default ({ app: { $axios } }, inject) => {
  const authService = new AuthService($axios);
  const userService = new UserService($axios);

  inject('authService', authService);
  inject('userService', userService);
}