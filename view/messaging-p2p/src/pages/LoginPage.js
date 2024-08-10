import React, { useState } from 'react';
import './LoginPage.css';
import { useNavigate } from 'react-router-dom';

function LoginPage({handleLogin}) {
    const navigate = useNavigate();
    const [identifier, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');


    const handleSubmit = async (e) => {
        e.preventDefault();
        const success = await handleLogin({ identifier, password });
        if (!success) {
            setErrorMessage('Invalid credentials. Please try again.');
            return;
        }
    };
    
    return (
        <div className="login-container">
        <form className="login-form" onSubmit={handleSubmit}>
            <h2>Login</h2>
            <div className="form-group">
            <label htmlFor="username">Username or Email:</label>
            <input
                type="text"
                id="identifier"
                value={identifier}
                onChange={
                    (e) => {
                        setUsername(e.target.value);
                        setErrorMessage('');
                    }
                }
                required
            />
            </div>
            <div className="form-group">
            <label htmlFor="password">Password:</label>
            <input
                type="password"
                id="password"
                value={password}
                onChange={
                    (e) => {
                        setPassword(e.target.value)
                        setErrorMessage('');
                    }
                }
                required
            />
            </div>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            <button type="submit" className="login-button">Login</button>
            <p className="register-link">
                Don't have an account? <a href="/register">Register here</a>
            </p>
        </form>
        </div>
    );
}

export default LoginPage;