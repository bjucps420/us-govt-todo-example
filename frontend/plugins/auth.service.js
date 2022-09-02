export class AuthService {
  constructor (axios) {
    this.axios = axios
  }

  async startForgotPassword(username) {
    const response = await this.axios.get(`/api/auth/forgot-password?user=${username}`);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async login(data) {
    const response = await this.axios.post(`/api/auth/login`, data);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async register(data) {
    const response = await this.axios.post(`/api/auth/register`, data);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async logout() {
    const response = await this.axios.get(`/api/auth/logout`);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }
}
