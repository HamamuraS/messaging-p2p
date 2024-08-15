import { API_URL } from '../utils/config'; 

const storage = {
  setItem: (key, value) => {
    try {
      localStorage.setItem(key, value);
    } catch (e) {
      console.warn('localStorage not available. Using in-memory storage.');
      storage[key] = value;
    }
  },
  getItem: (key) => {
    try {
      return localStorage.getItem(key);
    } catch (e) {
      return storage[key];
    }
  }
};

export const handleLogin = async ({ identifier, password }) => {
  try {
    const data = await login({ identifier, password });
    storage.setItem('token', data.token);
    storage.setItem('id', data.id);
    storage.setItem('username', data.username);
    storage.setItem('email', data.email);
    return true;
  } catch (error) {
    console.error('Login failed:', error.message);
    return false;
  }
};

const login = async (credentials) => {
  try {
    const response = await fetch(`${API_URL}/api/auth/login`, {
      
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    console.log('Response status:', response.status);

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(`Login failed with status ${response.status}: ${errorMessage}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Login petition failed:', error);
    throw error; 
  }
};