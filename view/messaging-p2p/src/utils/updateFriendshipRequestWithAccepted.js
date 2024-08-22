import axios from "axios";
import { API_URL } from "./config";

async function updateFriendshipRequestWithAccepted(accepted, requestId) {
  
  const newChannel = await axios
    .patch(
      `${API_URL}/api/friendship-requests`, 
      null,
      {
        params: {
          friendshipRequestId: requestId,
          accepted: accepted
        },
        headers: {Authorization: `Bearer ${localStorage.getItem('token')}`}
      }
    )
    .catch((error) => {
      console.error(error);
      return null;
    });
    
  return newChannel.data;
}

export default updateFriendshipRequestWithAccepted;