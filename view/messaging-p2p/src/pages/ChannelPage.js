import React, { useState, useEffect, useCallback, useMemo, useRef } from "react";
import './ChannelPage.css';
import {API_URL} from '../utils/config';

function ChannelPage() {
  const [channels, setChannels] = useState([]);
  const [activeChannel, setActiveChannel] = useState(null);
  const [messages, setMessages] = useState({});
  const [page, setPage] = useState({});
  const [totalPages, setTotalPages] = useState({});
  const [newMessage, setNewMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const chatMessagesRef = useRef(null);
  const isFetchingMore = useRef(false);
  const newContentHeight = useRef(0);

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };
  
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
  };
  
  const renderedMessages = useMemo(() => {
    if (!activeChannel || !messages[activeChannel.id] || messages[activeChannel.id].length === 0) {
      return null;
    };
    return messages[activeChannel.id].map(renderMessage);
  }, [activeChannel, messages]);
  
  const fetchMessages = useCallback(async (channel, pageNum = 0) => {
    if (isLoading || isFetchingMore.current) return;
    isFetchingMore.current = true;
    setIsLoading(true);
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `${API_URL}/api/channels/${channel.id}/messages?page=${pageNum}`,
        {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      const data = await response.json();
      
      setMessages(prev => {
        const newMessages = data.content.filter((message) => !prev[channel.id]?.some((m) => m.messageId === message.messageId));
        const updatedMessages = pageNum === 0 ? data.content : [...(prev[channel.id] || []), ...newMessages];
        
        if (pageNum !== 0) {
          setTimeout(() => {
            const messageElements = chatMessagesRef.current.children;
            newContentHeight.current = Array.from(messageElements)
              .slice(0, newMessages.length)
              .reduce((total, el) => total + el.offsetHeight, 0);
          }, 0);
        }
        
        return { ...prev, [channel.id]: updatedMessages };
      });
      
      setPage(prev => ({ ...prev, [channel.id]: data.page.number }));
      setTotalPages(prev => ({ ...prev, [channel.id]: data.page.totalPages }));
    } catch (error) {
      console.error('Error fetching messages:', error);
    } finally {
      setIsLoading(false);
      isFetchingMore.current = false;
    }
  }, [isLoading]);
  
  const fetchChannels = useCallback(async () => {
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
  }, []);

  const handleLoadMoreMessages = useCallback(() => {
    if (isFetchingMore.current) return;
    
    if (activeChannel && page[activeChannel.id] < totalPages[activeChannel.id] - 1) {
      console.log('Loading more messages...');
      fetchMessages(activeChannel, page[activeChannel.id] + 1);
    }
  }, [activeChannel, fetchMessages, page, totalPages]);
  
  const handleScroll = useCallback(() => {
    if (!chatMessagesRef.current || isLoading) return;
    
    const { scrollHeight, scrollTop, clientHeight } = chatMessagesRef.current;
    
    const loadMoreThreshold = 100;
    
    if (scrollHeight - scrollTop - clientHeight < loadMoreThreshold) {
      handleLoadMoreMessages();
    }
  }, [isLoading, handleLoadMoreMessages]);

  const handleChannelSelect = useCallback((channel) => {
    setActiveChannel(channel);
    if (!messages[channel.id]) {
      fetchMessages(channel);
    }
  }, [fetchMessages, messages]);
  
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
        setMessages(prev => ({
          ...prev,
          [activeChannel.id]: [messageData, ...(prev[activeChannel.id] || [])]
        }));
        setNewMessage('');
      } else {
        console.error('Error sending message:', response.statusText);
      }
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };
  
  useEffect(() => {
    if (chatMessagesRef.current) {
      chatMessagesRef.current.addEventListener('scroll', handleScroll);
    }
    return () => {
      if (chatMessagesRef.current) {
        chatMessagesRef.current.removeEventListener('scroll', handleScroll);
      }
    };
  }, [handleScroll]);
  
  useEffect(() => {
    fetchChannels();
  }, [fetchChannels]);
  
  useEffect(() => {
    if (newContentHeight.current > 0 && chatMessagesRef.current) {
      chatMessagesRef.current.scrollTop += newContentHeight.current;
      newContentHeight.current = 0;
    }
  }, [messages]);
  
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
      <div className="chat-container">
        {activeChannel ? (
          <>
            <h2>{activeChannel.name}</h2>
            <div className="chat-messages" ref={chatMessagesRef}>
              {renderedMessages || <p className="no-messages">No messages yet. Start the conversation!</p>}
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
    </div>
  );
}

export default ChannelPage;