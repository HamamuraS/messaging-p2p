import React, { useState } from "react";
import './ChannelPage.css';
import ChannelsList from '../components/ChannelsList';
import ChatContainer from "../components/ChatContainer";

function ChannelPage() {
  
  const [activeChannel, setActiveChannel] = useState(null);

  
  return (
    <div className="channels-page">
      <ChannelsList
        activeChannel={activeChannel}
        setActiveChannel={setActiveChannel}
      />
      <ChatContainer
        activeChannel={activeChannel}
      />
    </div>
  );
}

export default ChannelPage;