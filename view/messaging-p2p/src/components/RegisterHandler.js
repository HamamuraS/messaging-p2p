

export const handleRegister = async (credentials) => {
  
    const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
    });
    
    console.log('Response status:', response.status);
    
    return response;
    
};