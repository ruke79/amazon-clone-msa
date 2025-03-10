import React, { Fragment, useEffect, useRef, useState } from "react";

import { Outlet,  } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import { StompProvider } from "../../util/stompProvider";
import List from "components/chat/list/List";

import ChatArea from "components/chat/chatting/ChatArea";

import styled from "styled-components";
import ChatDetail from "components/chat/detail/ChatSidebar";
import ChatUserinfo from "components/chat/list/ChatUserinfo";
import { useSelector } from "react-redux";
import { selectedRoom } from "../../redux/ChatSelectors";


const Wrapper = styled.div`
  min-width: 100vw;
  min-height: 100vh;     
  max-width: 100vw;
  max-height: 100vh;     

  background-color: #F5F6FA;
  backdrop-filter: blur(19px) saturate(180%);    
  border: 1px solid #ffffff20;
  display: flex; 
  overflow : hidden;
`;

const ListWrapper = styled.div`

flex: 1;
display: flex;
flex-direction: column;    
`;

 export const ChatPage =() =>  {
    // no dispatch because of websoket
         
    const { token } = useAuthContext();
    const brokerURL = process.env.REACT_APP_API_URL + '/chat-service/chat';
    const connectHeaders = { Authorization: token }
    
           
    return (
      
      
      <StompProvider brokerURL={brokerURL} connectHeaders={connectHeaders}>            
      <Wrapper>
        <ListWrapper>
          <ChatUserinfo/>
          <Outlet/>
        </ListWrapper>     
    
        <ChatArea />         
         <ChatDetail /> 
        </Wrapper>
      </StompProvider>
      
    );

};

