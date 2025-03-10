import React, { Fragment, useEffect, useRef, useState } from "react";
import ChatArea from "components/chat/chat/ChatArea";
//import { MessageSearch } from "./MessageSearch";

import { Outlet, Route,  Routes } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import { useDispatch, useSelector } from "react-redux";
import { rooms, selectedRoom } from "../../redux/ChatSelectors";
import { StompProvider, useStomp } from "../../util/stompProvider";
import tokenUtil from "util/tokenUtil";
import List from "components/chat/list/List";
import Chat from "components/chat/chat/Chat";
import Detail from "components/chat/detail/Detail";

import { Stomp, Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { connect } from "formik";
import styled from "styled-components";


const Wrapper = styled.div`
  min-width: 100vw;
  min-height: 100vh;     
  max-width: 100vw;
  max-height: 100vh;     

   background-color: rgba(17, 25, 40, 0.75);
    backdrop-filter: blur(19px) saturate(180%);
    //border-radius: 12px;
    border: 1px solid #ffffff20;
    display: flex; 
    overflow : hidden;
`;

 export const ChatPage =() =>  {
  
   const currentRoom = useSelector(selectedRoom);

   

  const token = tokenUtil.getToken();

       
    const brokerURL = process.env.REACT_APP_API_URL + '/chat-service/chat';
    const connectHeaders = { Authorization: token }

   
    return (

      <StompProvider brokerURL={brokerURL} connectHeaders={connectHeaders} >
      
      <Wrapper>
      <List/>               
        { currentRoom ? <ChatArea room={currentRoom}/>
        : <div></div> }
         <Detail /> 
        </Wrapper>
      </StompProvider>
    
    );

};

