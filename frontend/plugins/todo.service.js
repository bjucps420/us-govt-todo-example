export class TodoService {
  constructor (axios) {
    this.axios = axios
  }

  async get(id) {
    const response = await this.axios.get(`/api/todo/${id}`);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async create(data) {
    const response = await this.axios.post('/api/todo/create', data);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async update(data) {
    const response = await this.axios.post('/api/todo/update', data);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  async delete(data) {
    const response = await this.axios.post('/api/todo/delete', data);
    if(response.status === 200) {
      return response.data;
    }
    return null;
  }

  list(status, options) {
    const append = [];
    if(options.search) {
      append.push(`search=${options.search}`);
    }
    if(options.groupBy) {
      append.push(`groupBy=${options.groupBy.join(',')}`);
    }
    if(options.groupDesc) {
      append.push(`groupDesc=${options.groupDesc.join(',')}`);
    }
    if(options.sortBy) {
      append.push(`sortBy=${options.sortBy.join(',')}`);
    }
    if(options.sortDesc) {
      append.push(`sortDesc=${options.sortDesc.join(',')}`);
    }
    if(options.page) {
      append.push(`page=${options.page}`);
    }
    if(options.mustSort) {
      append.push(`mustSort=${options.mustSort}`);
    }
    if(options.multiSort) {
      append.push(`multiSort=${options.multiSort}`);
    }
    if(options.itemsPerPage) {
      append.push(`itemsPerPage=${options.itemsPerPage}`);
    }
    return this.axios.get(`/api/todo/list/${status}?${append.join('&')}`);
  }
}
