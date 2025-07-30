// src/api/axiosConfig.js
import axios from 'axios';

const instance = axios.create({
  baseURL: process.env.REACT_APP_API_URL
});

// Request interceptor
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor
instance.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location = '/login';
    }
    return Promise.reject(error);
  }
);

export default instance;
