import axios from "axios";
import { API_URL } from "./config";


async function getPendingsNotifications() {
  try {
    const response = await axios.get(
      `${API_URL}/api/friendship-requests`,
      {
        params: { receiverId: localStorage.getItem('id') },
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      }
    );
    return response.data;
  } catch (err) {
    console.log('Error:', err.response ? err.response.data : err.message);
    return [];
  }
}
export default getPendingsNotifications;