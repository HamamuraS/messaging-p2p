
import React, { useMemo, useState, useEffect } from 'react';
import { API_URL } from '../utils/config';
import InfiniteScroll from 'react-infinite-scroll-component';
import axios from 'axios';

function ChatContainer({activeChannel}) {
    
    const [newMessage, setNewMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const [hasMoreMessages, setHasMoreMessages] = useState(true);
    const [nextPage, setNextPage] = useState(0);
    
    const formatTimestamp = (timestamp) => {
        const date = new Date(timestamp);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      };
      
    
    const renderedMessages = useMemo(() => {
        
        const renderMessage = (message) => {
            const isCurrentUser = String(message.senderId) === String(localStorage.getItem('id'));
            const messageClass = isCurrentUser ? 'user-message' : 'other-message';
    
            return (
                <div key={message.messageId} className={`message ${messageClass}`}>
                {!isCurrentUser && <div className="sender-name">{message.sender}</div>}
                <p className="message-content">{message.content}</p>
                <span className="message-time">{formatTimestamp(message.timestamp)}</span>
                </div>
            );
        }
        
        if (!activeChannel || messages.length === 0) {
          return null;
        }
        
        return messages.map(renderMessage);
        
      }, [activeChannel, messages]);
    
      
      
      
    const handleSendMessage = async () => {
        if (!newMessage.trim() || !activeChannel) return;
    
        try {
          const token = localStorage.getItem('token');
          const response = await fetch(
            `${API_URL}/api/messages`,
            {
              method: 'POST',
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                channel: activeChannel.id,
                content: newMessage,
                timestamp: new Date().toISOString()
              })
            }
          );
    
          if (response.ok) {
            const messageData = await response.json();
            setMessages(prev => [messageData, ...prev]);
            setNewMessage('');
          } else {
            console.error('Error sending message:', response.statusText);
          }
        } 
        catch (error) {
          console.error('Error sending message:', error);
        }
    };
    
    const fetchMoreData = async () => {
        if (!activeChannel) return;
        
        axios
            .get(
                `${API_URL}/api/channels/${activeChannel.id}/messages`,
                {
                    params: { page: nextPage },
                    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
                }
            )
          .then((res) => {
            const responseMessages = res.data.content;
            setMessages(prev => [...prev, ...responseMessages]);
    
            if (res.data.page.totalPages > res.data.page.number + 1) {
                setHasMoreMessages(true);
                setNextPage(prev => prev + 1);
            } else {
                setHasMoreMessages(false);
            }
            
          })
          .catch((err) => console.log(err));
    
    };
    
    useEffect(() => {
        if (activeChannel) {
            setNextPage(0);
            setMessages([]);
            axios.get(
                `${API_URL}/api/channels/${activeChannel.id}/messages`,
                {
                    params: { page: 0 },
                    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
                }
            )
            .then((res) => {
                const responseData = res.data;
                const responseMessages = responseData.content;
                setMessages(prevMessages => [...prevMessages, ...responseMessages]);
                
                if (res.data.page.totalPages > res.data.page.number + 1) {
                    setHasMoreMessages(true);
                    setNextPage(prev => prev + 1);
                } else {
                    setHasMoreMessages(false);
                }
            })
            .catch((err) => {
                console.log('Error:', err.response ? err.response.data : err.message);
            });
        }
    }, [activeChannel]);
    
    
    return (
        <div className="chat-container">
        {activeChannel ? (
        <>
            <h2>{activeChannel.name}</h2>

                <div className="chat-messages" id='scrollable-chat-container'>
                <InfiniteScroll
                    dataLength={messages.length || 0}
                    style={
                        {
                            display: 'flex', 
                            flexDirection: 'column-reverse'
                        }
                    }
                    next={fetchMoreData}
                    hasMore={hasMoreMessages}
                    loader={<h4>Loading...</h4>}
                    inverse={true}
                    scrollableTarget="scrollable-chat-container"
                >
                    {renderedMessages || <p className="no-messages">No messages yet. Start the conversation!</p>}
                </InfiniteScroll>
                </div>
                
            <div className="chat-input-container">
            <textarea
                placeholder="Type your message..."
                className="chat-input"
                rows={1}
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                onInput={(e) => {
                e.target.style.height = 'auto';
                const scrollHeight = e.target.scrollHeight;
                e.target.style.height = `${Math.min(scrollHeight, 150)}px`;
                e.target.style.overflowY = scrollHeight > 150 ? 'auto' : 'hidden';
                }}
                onKeyDown={(e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                }
                }}
            />
            <button className="send-button" onClick={handleSendMessage}>Send</button>
            </div>
        </>
        ) : (
        <p>Select a channel to start chatting</p>
        )}
    </div>
    );
}

export default ChatContainer;