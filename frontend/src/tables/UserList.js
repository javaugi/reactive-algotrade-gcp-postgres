// src/tables/UserList.js
import { useContext, useEffect, useState } from 'react';
import axios from '../api/axiosConfig';
import { AuthContext } from '../auth/AuthContext';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const { logout } = useContext(AuthContext);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const { data } = await axios.get('/api/user');
        setUsers(data);
      } catch (err) {
        console.error('Failed to fetch users:', err);
      }
    };
    fetchUsers();
  }, []);

  return (
    <div>
      <button onClick={logout}>Logout</button>
      {/* Render users */}
    </div>
  );
};

export default UserList;
