import React, { useEffect, useRef, useState } from "react";
import './ChannelPage.css';
import ChannelsList from '../components/ChannelsList';
import ChatContainer from "../components/ChatContainer";
import { WS_URL } from "../utils/config";

function ChannelPage() {
  const [activeChannel, setActiveChannel] = useState(null);
  const [messages, setMessages] = useState([]);
  const socketRef = useRef(null);
  const userId = localStorage.getItem('id');

  const activeChannelRef = useRef(activeChannel);
  
  useEffect(() => {
    activeChannelRef.current = activeChannel;
  }, [activeChannel]);
  
  useEffect(() => {
    let isMounted = true;
    
    const connectWebSocket = () => {
      if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
        return;
      }

      const newSocket = new WebSocket(`${WS_URL}/ws?userId=${userId}`);
      
      newSocket.addEventListener("open", (event) => {
        if (isMounted) {
          console.log('WebSocket connected');
          socketRef.current = newSocket;
        }
      });


      newSocket.addEventListener("message", (event) => {
        if (isMounted) {
          console.log('Received message:', event.data);
          const data = JSON.parse(event.data);
          
          if (data.senderId.toString() === userId.toString()) {
            return;
          }
          
          if (data.channel === activeChannelRef.current?.id) {
            setMessages(prevMessages => [data, ...prevMessages]);
          }else {
            console.log('Received message for channel:', data.channel);
          }
        }      
      });

      newSocket.addEventListener("close", (event) => {
        if (isMounted) {
          // handle reconnection or connection lost
          console.log('WebSocket disconnected:', event);
        }
      });

      newSocket.addEventListener("error", (event) => {
        console.error('WebSocket Error:', event);
      });
    };

    connectWebSocket();

    return () => {
      isMounted = false;
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, [userId]);


  return (
    <div className="channels-page">
      <ChannelsList
        activeChannel={activeChannel}
        setActiveChannel={setActiveChannel}
      />
      <ChatContainer
        activeChannel={activeChannel}
        messages={messages}
        setMessages={setMessages}
      />
    </div>
  );
}

export default ChannelPage;