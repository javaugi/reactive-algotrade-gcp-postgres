// src/App.js
import React, { useState, useEffect, Fragment } from 'react';
import axios from 'axios';
import logo from './logo.svg';
import './App.css';
import AddUser from './forms/AddUserForm';
import EditUser from './forms/EditUserForm';
import UserTable from './tables/UserTable';
import { Button, Container } from 'reactstrap';

// Create an Axios instance with base URL
const api = axios.create({
  baseURL: '/api', // Your API base URL
  timeout: 5000, // Request timeout
  headers: {
    'Content-Type': 'application/json'
  }
});

function App() {
    const [message, setMessage] = useState('');
    const [users, setUsers] = useState([]);
    const [currentUser, setCurrentUser] = useState({ id: null, name: '', username: '' });
    const [editing, setEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch health message
    const hello = () => {
        api.get('/health')
            .then(response => {
                setMessage(response.data);
            })
            .catch(err => {
                setError('Failed to fetch health status');
                console.error('Health check error:', err);
            });
    };

    // Fetch users from API
    const fetchUsers = () => {
        setLoading(true);
        setError(null);
        api.get('/user')
            .then(response => {
                setUsers(response.data);
            })
            .catch(err => {
                setError('Failed to load users');
                console.error('Error fetching users:', err);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    // Call APIs when component mounts
    useEffect(() => {
        hello();
        fetchUsers();
    }, []);

    // CRUD operations
    const addUser = user => {
        if (!user.name || !user.username) {
            alert('Please fill all fields');
            return;
        }        
        
        setError(null);
        api.post('/user', user)
            .then(response => {
                setUsers([...users, response.data]);
            })
            .catch(err => {
                setError('Failed to add user');
                console.error('Error adding user:', err);
            });
    };

    const deleteUser = id => {
        setError(null);
        api.delete(`/user/${id}`)
            .then(() => {
                setUsers(users.filter(user => user.id !== id));
                setEditing(false);
            })
            .catch(err => {
                setError('Failed to delete user');
                console.error('Error deleting user:', err);
            });
    };

    const updateUser = (id, updatedUser) => {
        setError(null);
        api.put(`/user/${id}`, updatedUser)
            .then(response => {
                setUsers(users.map(user => (user.id === id ? response.data : user)));
                setEditing(false);
            })
            .catch(err => {
                setError('Failed to update user');
                console.error('Error updating user:', err);
            });
    };

    const editRow = user => {
        setEditing(true);
        setCurrentUser({ id: user.id, name: user.name, username: user.username });
    };

    return (
        <>
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo" />
                    <h1 className="App-title">{message}</h1>
                </header>
            </div>      
            <div className="container">
                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}
                {loading ? (
                    <div className="text-center">
                        <div className="spinner-border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                        <p>Loading users...</p>
                    </div>
                ) : (
                    <div className="flex-row">
                        <div className="flex-large">
                            <Fragment>
                                <h3>Add user</h3>
                                <AddUser addUser={addUser} />
                            </Fragment>
                        </div>
                        <div className="flex-large">
                            <Fragment>
                                <h3>Edit user</h3>
                                <EditUser
                                    editing={editing}
                                    setEditing={setEditing}
                                    currentUser={currentUser}
                                    updateUser={updateUser}
                                />
                            </Fragment>
                        </div>
                        <div className="flex-large">
                            <h3>View users</h3>
                            <UserTable users={users} editRow={editRow} deleteUser={deleteUser} />
                        </div>
                    </div>
                )}
            </div>              
        </>
    );
}

export default App;

/*
Key Changes Made:

State Management:
    Removed hardcoded usersData
    Added loading state to handle API loading state
    Initialize users as empty array
API Integration:
    Added fetchUsers() function to get users from /api/user
Updated CRUD operations to use API endpoints:
    POST /api/user for adding users
    PUT /api/user/:id for updating users
    DELETE /api/user/:id for deleting users
UI Improvements:
    Added loading state display
    Maintained all existing functionality but now backed by API
    Error Handling:
    Basic error handling with catch for fetch operations
Assumptions About Your API:
    Your API endpoints follow REST conventions:
        GET /api/user - Returns array of users
        POST /api/user - Creates new user (expects user data in body)
        PUT /api/user/:id - Updates user (expects updated user data in body)
        DELETE /api/user/:id - Deletes user
        Users have id, name, and username properties
        API returns JSON responses
    Further Improvements:
    Add better error handling:
        jsx
        .catch(error => {
            console.error('Operation failed:', error);
            // Show error message to user
        });
    Add validation:
        jsx
        const addUser = user => {
            if (!user.name || !user.username) {
                alert('Please fill all fields');
                return;
            }
            // ... rest of the code
        };
    Use async/await syntax for cleaner code:
        jsx
        const fetchUsers = async () => {
            setLoading(true);
            try {
                const response = await fetch('/api/user');
                const data = await response.json();
                setUsers(data);
            } catch (error) {
                console.error('Error fetching users:', error);
            } finally {
                setLoading(false);
            }
        };
    Consider using a library like Axios for HTTP requests

This implementation should work with your existing component structure while moving all data operations to your backend API.


Key Improvements with Axios:
    Created Axios Instance:
    Configured base URL and default headers
    Set a request timeout
Better Error Handling:
    Added error state to display API errors to users
    Consistent error handling pattern across all requests
Response Data Handling:
    Axios automatically parses JSON responses (no need for .json())
    Response data is available in response.data
Loading States:
    Improved loading indicator with a spinner
    Uses finally() to ensure loading state is always reset
Request Cancellation:
    Axios supports request cancellation (though not implemented here)
Interceptors:
    The instance setup makes it easy to add request/response interceptors later
*/