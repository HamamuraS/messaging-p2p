import React, { useEffect, useState } from "react";
import { API_URL } from "../utils/config";
import UserBar from "./UserBar";
import { IoIosAdd } from "react-icons/io";

function ChannelsList({ 
    activeChannel,
    setActiveChannel
}) {
    
    const [channels, setChannels] = useState([]);
    
    const handleChannelSelect = (channel) => {
        setActiveChannel(channel);
        // now fetchMessages since activeChannel has changed
    };
    
    useEffect(() => {
        const fetchChannels = async () => {
            try {
              console.log('fetching channels');
              const userId = localStorage.getItem('id');
              const token = localStorage.getItem('token');
              const response = await fetch(
                  `${API_URL}/api/users/${userId}/channels`,
                  {
                      method: 'GET',
                      headers: {
                          'Authorization': `Bearer ${token}`,
                          'Content-Type': 'application/json'
                      }
                  }
              );
      
              const data = await response.json();
              setChannels(data.content);
            } catch (error) {
              console.error('Error fetching channels:', error);
            }
        };

        fetchChannels();
    }, []);
    
    return (
        <div className="d-flex flex-column h-100 border-right">
          
        <UserBar />
        
        <div className="flex-grow-1 overflow-auto bg-light">
          {channels.map((channel) => (
            <div
              key={channel.id}
              className={`channel-item ${channel.id === activeChannel?.id ? 'active' : ''}`}
              onClick={() => handleChannelSelect(channel)}
            >
              {channel.name} ({channel.type})
            </div>
          ))}
        </div>
        
        <div className="d-flex align-items-center p-2 bg-light border-top">
          <input
            type="text"
            placeholder="Type username or email..."
            className="form-control me-2"
          />
          <button className="btn btn-dark"><IoIosAdd/></button>
        </div>
      </div>
    );
    
    
    
}

export default ChannelsList;