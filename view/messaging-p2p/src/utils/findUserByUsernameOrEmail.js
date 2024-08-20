import axios from "axios";
import { API_URL } from "./config";

/*
    returns user data if found, null otherwise
*/
async function findUserByUsernameOrEmail(usernameOrEmail) {
    
    try {
        const res = await axios.get(
            `${API_URL}/api/users/search`,
            {
                params: {
                    username: usernameOrEmail,
                    email: usernameOrEmail
                },
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
            }
        );
        return res.data;
    } catch (err) {
        console.log('Error:', err.response ? err.response.data : err.message);
        return null;
    }
    
};

export default findUserByUsernameOrEmail;