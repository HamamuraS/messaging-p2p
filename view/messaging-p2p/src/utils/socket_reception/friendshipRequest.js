

function friendshipRequest(data, setNotifications) {
  const userId = localStorage.getItem('id');
  
  if (data.senderId.toString() === userId.toString()) {
    return;
  }
  setNotifications(prevNotifications => [data, ...prevNotifications]);
  console.log('Friendship request received from user ', data.senderId);
}


export default friendshipRequest;