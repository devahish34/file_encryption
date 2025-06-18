import axios from 'axios';

const API_URL = '/api/auth/';

// Setup axios interceptor for authentication
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const authService = {
  register: async (userData) => {
    const response = await axios.post(API_URL + 'register', userData);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  },

  login: async (email, password) => {
    const response = await axios.post(API_URL + 'login', { email, password });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
  },

  getCurrentUser: async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }
    try {
      const response = await axios.get(API_URL + 'user');
      return response.data;
    } catch (error) {
      localStorage.removeItem('token');
      throw error;
    }
  },

  // OAuth related functionality
  getOAuthLoginUrl: (provider) => {
    return `/oauth2/authorization/${provider}`;
  }
};

export default authService;