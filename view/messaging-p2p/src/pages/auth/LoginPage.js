import React, { useState } from 'react';
import './Forms.css';
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
        navigate('/channels');
    };
    
    return (
        <div className="form-page-container">
        <form className="form" onSubmit={handleSubmit}>
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
                        errorMessage && setErrorMessage('');
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
                        setPassword(e.target.value);
                        errorMessage && setErrorMessage('');
                    }
                }
                required
            />
            </div>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            <button type="submit" className="submit-button">Login</button>
            <p className="extern-link">
                Don't have an account? <a href="/register">Register here</a>
            </p>
        </form>
        </div>
    );
}

export default LoginPage;