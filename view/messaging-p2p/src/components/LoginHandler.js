

export const handleLogin = async ({ identifier, password }) => {
    try {
        const data = await login({ identifier, password });
        localStorage.setItem('token', data.token);
        localStorage.setItem('id', data.id);
        localStorage.setItem('username', data.username);
        localStorage.setItem('email', data.email);
    
        return true;
    }
    catch (error) {
        console.error(error.message);
        return false;
    }
    
};

const login = async (credentials) => {
    const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
    });
    console.log('Response status:', response.status);
    
    if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(`Login failed with status ${response.status} and message \"${errorMessage}\"`);
    }
    
    return await response.json();
};


