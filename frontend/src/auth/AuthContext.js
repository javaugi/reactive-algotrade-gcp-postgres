// src/auth/AuthContext.js
import { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Update checkAuth to handle 401 responses
    const checkAuth = async () => {
        const token = localStorage.getItem('token');

        if (!token) {
          setLoading(false);
          return;
        }

        try {
          const response = await fetch('/api/user', {
            headers: {
              'Authorization': `Bearer ${token}`  // MUST include this
            },
            credentials: 'include'
          });

          if (response.ok) {
            const user = await response.json();
            setUser(user);
          }
        } catch (error) {
          localStorage.removeItem('token');
        } finally {
          setLoading(false);
        }
      };
  }, []);

  const login = async (username, password) => {
    const { data } = await axios.post('/api/auth/login', { username, password });
    localStorage.setItem('token', data.token);
    axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    setUser(data.user);
  };

  const logout = () => {
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
    setUser(null);
  };
  
  const value = {
    user,
    isAuthenticated: !!user, // Add this for easier auth checks
    login,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

// Create a custom hook for consuming the context
export const useAuth = () => {
  return useContext(AuthContext);
};

/*
useEffect(() => {
    const checkAuth = async () => {
      try {
        const token = localStorage.getItem('token');
        if (token) {
          axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
          const { data } = await axios.get('/api/auth/me');
          setUser(data);
        }
      } finally {
        setLoading(false);
      }
    };
    checkAuth();
  }, []); 
*/