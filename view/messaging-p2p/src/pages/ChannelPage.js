import { useState, useEffect } from "react";

function ChannelPage({ onChannelSelect }) {
    
    const [channels, setChannels] = useState([]);
    const [activeChannel, setActiveChannel] = useState(null);

    useEffect(() => {   
        const fetchChannels = async () => {
          try {
            const userId = localStorage.getItem('id');
            const response = await fetch('http://localhost:8080/api/users/'+userId+'/channels'); // Cambia la URL por la de tu API
    
            const data = await response.json();
            setChannels(data.content);
 
          } catch (error) {
            console.error('Error fetching channels:', error);
          }
        };
        fetchChannels();
    }, []);
    
    const handleChannelSelect = (channel) => {
        setActiveChannel(channel);
        onChannelSelect(channel);
      };
    
      return (
        <div className="channels-page">
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
          </div>
          <div className="chat-container">
            {activeChannel ? (
              <>
                <h2>{activeChannel.name}</h2>
                <div className="chat-messages">
                  <p>Chat messages would go here...</p>
                </div>
              </>
            ) : (
              <p>Select a channel to start chatting</p>
            )}
          </div>
        </div>
      );
    
}

export default ChannelPage;