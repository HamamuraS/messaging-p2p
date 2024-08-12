import { useState, useEffect } from "react";
import './ChannelPage.css';

function ChannelPage({ onChannelSelect }) {
    
    const [channels, setChannels] = useState([]);
    const [activeChannel, setActiveChannel] = useState(null);

    useEffect(() => {   
        const fetchChannels = async () => {
          try {
            const userId = localStorage.getItem('id');
            const token = localStorage.getItem('token');
            const response = await fetch(
                'http://localhost:8080/api/users/'+userId+'/channels',
                {
                    method: 'GET',
                    headers: {
                        'Authorization': 'Bearer ' + token,
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
                <div className="chat-input-container">
                <textarea
                    placeholder="Type your message..."
                    className="chat-input"
                    rows={1}
                    onInput={(e) => {
                        e.target.style.height = 'auto';
                        const scrollHeight = e.target.scrollHeight;
                        e.target.style.height = `${Math.min(scrollHeight, 150)}px`;
                        e.target.style.overflowY = scrollHeight > 150 ? 'auto' : 'hidden';
                    }}
                ></textarea>
                <button className="send-button">Send</button>
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