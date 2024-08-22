import React, {useEffect, useState} from 'react';
import { FiUser } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { FiLogOut } from 'react-icons/fi';
import FindPeoplePane from './FindPeoplePane';
import NotificationsPane from './NotificationsPane';
import { Badge } from '@mui/material';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import AddIcon from '@mui/icons-material/Add';
import getPendingsNotifications from '../utils/getPendingNotifications';

const UserBar = ({notifications, setNotifications, setChannels}) => {
  const username = localStorage.getItem('username');
  const navigate = useNavigate();
  const [showFindPeoplePane, setShowFindPeoplePane] = useState(false);
  const [showNotificationsPane, setShowNotificationsPane] = useState(false);

  
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/login');
  };
  
  const handleFindPeople = () => {
    setShowFindPeoplePane(true);
  };
  
  const handleDisplayNotifications = () => {
    setShowNotificationsPane(true);
  };
  
  
  useEffect(() => {

    const fetchNotifications = async () => {
      //gets notifications that still have not been accepted or rejected
      const pendingNotifications = await getPendingsNotifications();
      setNotifications(pendingNotifications);
      if (pendingNotifications.length > 0) {
        //notify user
        console.log('User has pending notifications!');
        console.log('example notification:', pendingNotifications[0]);
      } 
    };
    
    fetchNotifications();
    
  }, []);
  
  return (
    <div className="d-flex text-light bg-dark justify-content-between align-items-center flex-shrink-0 p-3 border-bottom">
        
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
          <button className="btn btn-dark" title="Notifications"
          onClick={handleDisplayNotifications}>
          <Badge badgeContent={notifications.length} color="primary">  
            <NotificationsNoneIcon />
          </Badge>
          </button>
          <button 
            className="btn btn-dark" title="Find people"
            onClick={handleFindPeople}>
            <AddIcon />
          </button>
          <button 
              className="btn btn-danger logout-btn" 
              onClick={handleLogout}><FiLogOut />
          </button>
        </div>
        <FindPeoplePane open={showFindPeoplePane} setOpen={setShowFindPeoplePane} />
        <NotificationsPane 
          open={showNotificationsPane} 
          setOpen={setShowNotificationsPane}
          notifications={notifications}
          setNotifications={setNotifications} 
          setChannels={setChannels}
        />

    </div>
  );
};

export default UserBar;
