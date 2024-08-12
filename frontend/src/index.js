import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

import './index.css';
import { ContextProvider } from "./store/AuthContext";


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ContextProvider>
    <App />
   </ContextProvider>  
);

