import React, { useEffect, useRef, useState, useCallback } from "react";
import './ChannelPage.css';
import ChannelsList from '../components/ChannelsList';
import ChatContainer from "../components/ChatContainer";
import UserBar from "../components/UserBar";
import { WS_URL } from "../utils/config";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import friendshipRequest from "../utils/socket_reception/friendshipRequest";
import handleTextMessage from "../utils/socket_reception/handleTextMessage";
import { IconButton } from '@mui/material';
import { ChevronLeft, ChevronRight } from '@mui/icons-material';

function ChannelPage() {
  const [activeChannel, setActiveChannel] = useState(null);
  const [messages, setMessages] = useState([]);
  const socketRef = useRef(null);
  const userId = localStorage.getItem('id');
  const [isConnected, setIsConnected] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [channels, setChannels] = useState([]);
  const [isCollapsed, setIsCollapsed] = useState(false);
  
  const toggleSidebar = useCallback(() => {
    setIsCollapsed((prev) => !prev);
  } , []);
  
  // ======= syncronization protection ======
  const connectingRef = useRef(false);
  const connectPromiseRef = useRef(null);
  // ========================================
  
  const activeChannelRef = useRef(activeChannel);
  
  useEffect(() => {
    activeChannelRef.current = activeChannel;
  }, [activeChannel]);
  

  
  const connectWebSocket = useCallback(() => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      return Promise.resolve(socketRef.current);
    }

    if (connectingRef.current) {
      return connectPromiseRef.current;
    }
  

    connectingRef.current = true;
    connectPromiseRef.current = new Promise((resolve, reject) => {
      const newSocket = new WebSocket(`${WS_URL}/ws?userId=${userId}`);
      
      newSocket.addEventListener("open", (event) => {
        console.log('WebSocket connected');
        socketRef.current = newSocket;
        connectingRef.current = false;
        setIsConnected(true);
        resolve(newSocket);
      });

      newSocket.addEventListener("message", (event) => {
        console.log('Received message:', event.data);
        const data = JSON.parse(event.data);
        
        switch (data.messageType) {
          case "MESSAGE":
            handleTextMessage(data, setMessages, activeChannelRef);
            break;
          case "FRIENDSHIP_REQUEST":
            friendshipRequest(data, setNotifications);
            break;
          case "NEW_CHANNEL":
            setChannels((prevChannels) => [...prevChannels, data]);
            break;
          default:
            console.error('Unknown message type:', data.messageType);
            break;
        }
        
      });

      newSocket.addEventListener("close", (event) => {
        console.log('WebSocket disconnected:', event);
        socketRef.current = null;
        connectingRef.current = false;
        setIsConnected(false);
        if (localStorage.getItem('token')) {
          /*setTimeout(() => {
            connectWebSocket().catch(console.error);
          }, 2000);*/
        }

      });
      
      newSocket.addEventListener("error", (event) => {
        console.error('WebSocket Error:', event);
        connectingRef.current = false;
        reject(event);
      });
    });

    return connectPromiseRef.current;
  }, [userId]);

  useEffect(() => {
    connectWebSocket().catch(console.error);
    return () => {
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, [connectWebSocket]);
  
  const displayReconnection = useCallback(() => {
      
    if (isConnected) {
      toast.dismiss();
      return;
    }
    
    toast.error('Connection lost. Reconnecting...', {
      autoClose: false,
      closeButton: false,
      closeOnClick: false,
      draggable: false
    });
  }, [isConnected]);
  
  useEffect(() => {
    displayReconnection();
  }, [isConnected, displayReconnection]);

  return (
    <div className="channels-page d-flex">
    <div 
      className={`d-flex flex-column align-items-stretch flex-shrink-0 bg-body-tertiary ${isCollapsed?'collapsed':''}`}
      style={{ transition: 'width 0.3s', width: isCollapsed ? '2em' : '26.5em' }}
    >
    <IconButton onClick={toggleSidebar} style={{ alignSelf: 'flex-end' }}>
      {isCollapsed ? <ChevronRight /> : <ChevronLeft />}
    </IconButton>
        {!isCollapsed && (
          <>
            <UserBar 
              notifications={notifications}
              setNotifications={setNotifications}
              setChannels={setChannels}
            />
            <ChannelsList
              activeChannel={activeChannel}
              setActiveChannel={setActiveChannel}
              channels={channels}
              setChannels={setChannels}
            />
          </>
        )}
    </div>
      <ChatContainer
        activeChannel={activeChannel}
        messages={messages}
        setMessages={setMessages}
      />
      <ToastContainer/>
    </div>
  );
}

export default ChannelPage;