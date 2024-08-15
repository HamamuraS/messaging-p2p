import React, { useEffect, useState } from "react";
import { API_URL } from "../utils/config";

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
        <div className="channels-list">
        {channels.map((channel) => (
          <div
            key={channel.id}
            className={`channel-item ${channel.id === activeChannel?.id ? 'active' : ''}`}
            onClick={() => handleChannelSelect(channel)}
          >
            {channel.name} ({channel.type})
          </div>
        ))}
        <div className="channels-tools">
          <input
            type="text"
            placeholder="Search users..."
            className="search-input"
          />
          <button className="add-button">Add Channel</button>
          <button className="settings-button">Settings</button>
        </div>
      </div>
    );
    
    
    
}

export default ChannelsList;