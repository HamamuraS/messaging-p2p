
import axios from "axios";
import { API_URL } from "./config";
import Swal from "sweetalert2";

async function sendFriendRequest(userId) {
  await axios
  .post(
    `${API_URL}/api/friendship-requests`,
    null,
    {
      params: {
        senderId: localStorage.getItem('id'),
        receiverId: userId
      },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }
  )
  .then(res => {
    console.log(res.data);
    
    Swal.fire({
      title: 'Friendship requested!',
      icon: 'success',
      showConfirmButton: false,
      timer: 1000,
    });
    
  }).catch(err => {
    console.log('Error:', err.response ? err.response.data : err.message);
    
    Swal.fire({
      title: 'Friendship request failed!',
      text: err.response ? err.response.data.message : err.message,
      icon: 'error',
      showCloseButton: true
    });
    
  }
  );
}

export default sendFriendRequest;