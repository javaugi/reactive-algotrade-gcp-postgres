// src/App.js
import React, { useState, useEffect, Fragment } from 'react';
import logo from './logo.svg';
import './App.css';
import AddUser from './forms/AddUserForm'
import EditUser from './forms/EditUserForm'
import UserTable from './tables/UserTable'
import { Button, Container } from 'reactstrap';

function App() {
    const [message, setMessage] = useState('');

    const hello = () => {
        fetch('/api/health')
            .then(response => response.text())
            .then(message => {
                setMessage(message);
            });
    };

    // Call hello when component mounts
    useEffect(() => {
        hello();
    }, []);

    const usersData = [
        {id: 1, name: 'Tania', username: 'floppydiskette'},
        {id: 2, name: 'Craig', username: 'siliconeidolon'},
        {id: 3, name: 'Ben', username: 'benisphere'}
    ]; 

    const initialFormState = {id: null, name: '', username: ''};

    // Setting state
    const [users, setUsers] = useState(usersData);
    const [currentUser, setCurrentUser] = useState(initialFormState);
    const [editing, setEditing] = useState(false);

    // CRUD operations
    const addUser = user => {
        user.id = users.length + 1;
        setUsers([...users, user]);
    };

    const deleteUser = id => {
        setEditing(false);
        setUsers(users.filter(user => user.id !== id));
    };

    const updateUser = (id, updatedUser) => {
        setEditing(false);
        setUsers(users.map(user => (user.id === id ? updatedUser : user)));
    };

    const editRow = user => {
        setEditing(true);
        setCurrentUser({id: user.id, name: user.name, username: user.username});
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
        </div>              
        </>
        );
}

//export default App;
    
/*
import React, {Component} from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider, AuthContext } from './auth/AuthContext';
import PrivateRoute from './auth/PrivateRoute';
import Home from './Home';
import Login from './auth/Login';
import UserList from './tables/UserList';
import AddUser from './forms/AddUserForm';
import EditUser from './forms/EditUserForm';
import { CookiesProvider } from 'react-cookie';
import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <CookiesProvider>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} /> 
          <Route
            path="/users"
            element={
              <PrivateRoute>
                <UserList />
              </PrivateRoute>
            }
          />
          <Route
            path="/add-user"
            element={
              <PrivateRoute>
                <AddUser />
              </PrivateRoute>
            }
          />
          <Route
            path="/edit-user"
            element={
              <PrivateRoute>
                <EditUser />
              </PrivateRoute>
            }
          />
        </Routes>        
        </CookiesProvider>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

*/