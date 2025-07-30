// src/Home.js
import React, { useState, useEffect } from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies, useCookies } from 'react-cookie';

function Home(props) {
  const [state, setState] = useState({
    isLoading: true,
    isAuthenticated: false,
    user: undefined,
    csrfToken: props.cookies.get('XSRF-TOKEN')
  });

  const [cookies] = useCookies(['XSRF-TOKEN']);

  useEffect(() => {
    async function fetchUser() {
      const response = await fetch('/api/user', { credentials: 'include' });
      const body = await response.text();
      console.log('fetchUser body=' + body);
      if (body === '') {
        setState(prev => ({ ...prev, isAuthenticated: false }));
      } else {
        setState(prev => ({ ...prev, isAuthenticated: true, user: JSON.parse(body) }));
      }
    }
    fetchUser();
  }, []);

  const login = () => {
    console.log('login called');
    window.location.href = '/login'; // Simple redirect to login page
  };

  const logout = () => {
    console.log('logout called');
    fetch('/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
      headers: { 'X-XSRF-TOKEN': state.csrfToken }
    })
      .then(res => res.json())
      .then(response => {
        window.location.href = response.logoutUrl + "?id_token_hint=" +
          response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
      });
  };

  const message = state.user ?
    <h2>Welcome, {state.user.name}!</h2> :
    <p>Please log in to manage your app.</p>;

  const button = state.isAuthenticated ?
    <div>
      <Button color="link"><Link to="/groups">Manage App</Link></Button>
      <br/>
      <Button color="link" onClick={logout}>Logout</Button>
    </div> :
    <Button color="primary" onClick={login}>Login</Button>;

  return (
    <div>
      <AppNavbar/>
      <Container fluid>
        {message}
        {button}
      </Container>
    </div>
  );
}

export default withCookies(Home);
//export default Home;    
