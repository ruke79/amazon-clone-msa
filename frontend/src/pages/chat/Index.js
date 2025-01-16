import React, { createContext, useState } from "react";

import Sidebar from "components/chat/Sidebar";
import Header from "./Header";
import { ChatPage } from "./ChatPage";
import { Outlet } from "react-router-dom";


import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';

import { useDispatch, useSelector } from "react-redux";
import { ChatProvider } from "store/ChatContext";



function Chat() {
  const dispatch = useDispatch();
  const lightTheme = useSelector((state) => state.themeKey);
  const [refresh, setRefresh] = useState(true);

  return (
    
      <ChatProvider>
        <CssBaseline />
        <Box sx={{ display: 'flex', minHeight: '100dvh' }}>
          <Sidebar />
          <Header />
          <Box component="main" className="MainContent" sx={{ flex: 1 }}>
            <ChatPage />          
          </Box>
        </Box>    
      </ChatProvider>
  );
}

export default Chat;
