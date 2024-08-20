

function handleTextMessage(data, setMessages, activeChannelRef) {

  const userId = localStorage.getItem('id');
  
  if (data.senderId.toString() === userId.toString()) {
    return;
  }
  
  if (data.channel === activeChannelRef.current?.id) {
    setMessages(prevMessages => [data, ...prevMessages]);
  } else {
    console.log('Received message for channel:', data.channel);
  }
}

export default handleTextMessage;