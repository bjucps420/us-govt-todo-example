export class UserService {
  constructor (axios) {
    this.axios = axios
  }

  async getCurrent() {
    const response = await this.axios.get('/api/user/current');
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async getSecret() {
    const response = await this.axios.get('/api/user/get-secret');
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async toggleTwoFactor(enabling, secret, secretBase32, code) {
    const response = await this.axios.post('/api/user/toggle-two-factor', {
      code,
      secret,
      secretBase32,
      enableTwoFactor: enabling
    });
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async changeEmail(newEmail) {
    const response = await this.axios.post('/api/user/change-email', {
      newEmail
    });
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async changePassword(currentPassword, newPassword) {
    const response = await this.axios.post('/api/user/change-password', {
      currentPassword,
      newPassword
    });
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }
}
