
import React, { createContext, useState, useEffect } from "react";
import axios from "axios";  

import "./chat.css"
import { ChatPage } from "./ChatPage";
import { Outlet } from "react-router-dom";

import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';

import { useDispatch, useSelector } from "react-redux";
import { addRoomToList, setRooms } from "../../redux/ChatSlice";
import { useFetchChatRooms } from "../../hook/chatHooks";
import { useAuthContext } from "store/AuthContext";


function Chat() {
 
  return (
     
      <React.Fragment>                
            <ChatPage />              
        </React.Fragment>  
     
  );
}

export default Chat;
