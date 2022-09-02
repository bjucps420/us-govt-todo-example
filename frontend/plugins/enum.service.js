export class EnumService {
  constructor (axios) {
    this.axios = axios
  }

  async statuses() {
    const response = await this.axios.get('/api/enum/status/all');
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async types() {
    const response = await this.axios.get('/api/enum/type/all');
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }
}
