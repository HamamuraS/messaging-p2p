import { API_URL } from '../utils/config'; 

export const handleRegister = async (credentials) => {
  
    const response = await fetch(`${API_URL}/api/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
    });
    
    console.log('Response status:', response.status);
    
    return response;
    
};