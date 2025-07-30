//src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client'; // Notice the /client import
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
//import './api/axiosConfig'; // Import to activate interceptors
import App from './App';
import registerServiceWorker from './registerServiceWorker';

const root = ReactDOM.createRoot(document.getElementById('root')); // Create a root
root.render( // Render to the root
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
registerServiceWorker();


/*
 src/
├── auth/               # Auth-related files
│   ├── AuthContext.js  # React context for auth state
│   ├── Login.js        # Login component
│   └── PrivateRoute.js # Protected routes
├── api/
│   └── axiosConfig.js  # Axios instance with interceptors
├── App.js
├── index.js
 */