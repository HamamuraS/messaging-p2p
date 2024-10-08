import React, { useEffect, useState } from "react";
import { API_URL } from "../utils/config";

function ChannelsList({ 
    activeChannel,
    setActiveChannel,
    channels,
    setChannels
}) {

    
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
          
        <div className="list-group-flush border-bottom scrollarea">
          {channels.map((channel) => (
            <div
              key={channel.id}
              
              className={`channel-item ${channel.id === activeChannel?.id ? 'active' : ''} `}
              onClick={() => handleChannelSelect(channel)}
            >
              <div className="d-flex w-100 align-items-center justify-content-between">
                <strong className="mb-1">{channel.name}</strong>
                <small>date here</small>
              </div>
              <div className="col-10 mb-1 small">
                last message goes here
              </div>
            </div>

          ))}
        </div>
        
    );
    
    
    
}

export default ChannelsList;