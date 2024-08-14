import React from 'react';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import { Navigate, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { handleLogin } from './components/LoginHandler';
import { handleRegister } from './components/RegisterHandler';
import ChannelPage from './pages/ChannelPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage handleLogin={handleLogin}/>} />
        <Route path="/register" element={<RegisterPage handleRegister={handleRegister} />} />
        <Route path="/channels" element={<ChannelPage/>} />
        {/* Other routes */}
        
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
