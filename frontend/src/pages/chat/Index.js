import React, { createContext, useState } from "react";

import Sidebar from "components/chat/Sidebar";
import Header from "./Header";
import { ChatPage } from "./ChatPage";
import { Outlet } from "react-router-dom";


import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';

import { useDispatch, useSelector } from "react-redux";


export const myContext = createContext();
function Chat() {
  const dispatch = useDispatch();
  const lightTheme = useSelector((state) => state.themeKey);
  const [refresh, setRefresh] = useState(true);

  return (
    
    <myContext.Provider value={{ refresh: refresh, setRefresh: setRefresh }}>
      <CssBaseline />
      <Box sx={{ display: 'flex', minHeight: '100dvh' }}>
        <Sidebar />
        <Header />
        <Box component="main" className="MainContent" sx={{ flex: 1 }}>
          <ChatPage />          
        </Box>
      </Box>    
      </myContext.Provider>      
  );
}

export default Chat;
