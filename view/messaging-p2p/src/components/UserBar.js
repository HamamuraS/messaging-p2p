import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FiLogOut, FiSettings, FiBell, FiUser } from 'react-icons/fi';

const UserBar = () => {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <div className="bg-dark text-light py-2 px-3 d-flex justify-content-between align-items-center">
        
        {/* Foto de perfil y nombre de usuario */}
        <div className="d-flex align-items-center">
          <div className="profile-photo-placeholder me-3">
            <div
              className="bg-secondary text-center"
              style={{
                width: '40px',
                height: '40px',
                lineHeight: '40px',
                borderRadius: '50%',
              }}
            >
              <FiUser />
            </div>
          </div>
          <div className="fw-bold fs-5">{username}</div>
        </div>

        <div className="user-actions d-flex">
          <button className="btn btn-dark me-2" title="Notifications">
            <FiBell />
          </button>
          <button className="btn btn-dark me-2" title="Settings">
            <FiSettings />
          </button>
          <button className="btn btn-dark" title="Logout" onClick={handleLogout}>
            <FiLogOut />
          </button>
        </div>

    </div>
  );
};

export default UserBar;
