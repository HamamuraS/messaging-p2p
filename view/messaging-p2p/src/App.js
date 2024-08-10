import React from 'react';
import LoginPage from './pages/LoginPage';
import { Navigate, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { handleLogin } from './components/LoginHandler';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage handleLogin={handleLogin}/>} />

        {/* Other routes */}
        
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
