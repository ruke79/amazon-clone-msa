import React, { createRef, useContext, useLayoutEffect, useEffect, useRef, useState, memo } from "react";
import styled from 'styled-components';


import { useDispatch, useSelector } from "react-redux";
import { useTheme } from "@mui/material/styles";
import Skeleton from "@mui/material/Skeleton";

import { Stomp, Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";


import {
  selectedRoom, roomConversation, cursorId

} from "../../../redux/ChatSelectors"
import { useStomp } from "util/stompProvider";
import { useFetchRoomMessages, fetchMessages, usePrevState } from "hook/chatHooks";
import { SettingsPhoneSharp } from "@mui/icons-material";
import { selectRoom, setRoomConversation, fetchRoomConversation } from "../../../redux/ChatSlice";
import tokenUtil from "util/tokenUtil";
import { getRoomMessages } from "../../../util/apichat";


// import "./ChatArea.css"
import ChatHeader from "./ChatHeader";
import ChatFooter from "./ChatFooter";
import ChatContent from "./ChatContent";


const Chat = styled.div`
  flex: 2;
  border-left: 1px solidrgba(2, 2, 2, 0.21);
  border-right: 1px solid #dddddd35;  
  display: flex;
  flex-direction: column;  
   width: 100%;
   height: 100vh;
  background: #b2c7d9;  
`;



const Wrapper = styled.div`
  //  position: fixed;
  //  top: 0px;
  //  left: 0px;
  // z-index: 99;
  //  width: 100%;
  //  height: 100vh;
  //background: #b2c7d9;
`;

let prevScrollHeight = 0;

export function ChatArea({ room }) {

  const [isShowDownBtn, setIsShowDownBtn] = useState(false);
  const [sender, setSender] = useState(null);
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [loaded, setLoaded] = useState(false);
  let prevMessages = usePrevState(messages);

  const [prevRoom, setPrevRoom] = useState(room);

  
  const cursor = useRef('-1');
  const messageRef = useRef(null);

  const { roomMessages, isLoadinng, isFetch } = useFetchRoomMessages(room, cursor.current);

  if (room !== prevRoom) {
    setPrevRoom(room);
    
    setIsShowDownBtn(false);
    setSender(null);
    setMessage('');
    setMessages([]);
    setLoaded(false);
    cursor.current = '-1';    
    

  }



  const user = tokenUtil.getUser();
  const { isConnected, subscribe, sendMessage } = useStomp();


  if (isLoadinng) return <div><p>Loading...</p></div>;

  if (isFetch && loaded) {

    if (cursor.current === '-1') {
      setMessages(roomMessages);
    }
    else {
      console.log("Merged");
      const merged = [...roomMessages, ...messages];
      setMessages(merged);
    }

    setLoaded(false);
  }


  useEffect(() => {

    if (!messageRef.current) return;

    // 스크롤 변롸에 따른 action
    const handleScroll = () => {

      const scrollTop = messageRef.current?.scrollTop;

      // 스크롤을 맨 위로 올리면, 서버에서 이전 채팅을 불러옵니다.
      if (messages.length > 0 && scrollTop === 0) {

        cursor.current = messages[0].id;
        setLoaded(true);

        prevScrollHeight = messageRef.current?.scrollHeight;
      }

    }

    messageRef.current.addEventListener('scroll', handleScroll);

    changeScroll();

    return () => {
      try {
        messageRef.current.removeEventListener('scroll', handleScroll);
      } catch (e) {
        console.warn('could not removeEventListener on scroll');
      }
    };


  }, [messages, messageRef.current])





  // 스크롤을 가장 아래로 내립니다.
  const pageDown = () => {
    if (!messageRef.current) return;


    const scrollHeight = messageRef.current?.scrollHeight;
    messageRef.current.scrollTop = scrollHeight;
  };

  const changeScroll = () => {

    if (!messageRef.current || messages.length === 0) return;


    const numPrevChat = prevMessages.length;
    const numCurrChat = messages.length;
    const currScrollHeight = messageRef.current?.scrollHeight;
    
    if (numPrevChat !== numCurrChat) {

      if (numPrevChat === 0) {
        pageDown();
      } else {
        const prevLastChat = prevMessages[numPrevChat - 1];
        const currLastChat = messages[numCurrChat - 1];
        if (prevMessages[0].id !== messages[0].id) {
          messageRef.current.scrollTop = currScrollHeight - prevScrollHeight;
        }
        // 메시지 송수신 시 스크롤 변화
        else if (prevLastChat.id !== currLastChat.id) {
          // 채팅 수신 시, 채팅을 내가 보냈거나 스크롤이 밑에 있다면 스크롤을 가장 아래로 내립니다.
          if (
            currLastChat.email === user.email ||
            prevScrollHeight - messageRef.scrollTop <=
            messageRef.clientHeight + 100
          ) {
            pageDown();
          }
          // 스크롤이 위에 있다면, 채팅 왔다는 것을 알려주는 컴포넌트를 나타냅니다.
          else if (
            prevScrollHeight - messageRef.current?.scrollTop >
            messageRef.current?.clientHeight + 1000
          ) {
            setIsShowDownBtn(true);
            setSender(currLastChat.email);
            setMessage(currLastChat.content);
          }

        }
      }

      prevScrollHeight = currScrollHeight;
    }
  };


  // 구독
  useEffect(() => {
    let unsubscribe = () => { };

    if (isConnected) {


      const contentHeader =
        { roomId: room?.roomId, email: user.email };

      unsubscribe = subscribe(`/sub/chat/room/${room?.roomId}`, (message) => {
        const parsed = JSON.parse(message.body);
        //console.log("Received message:", parsed);
        setMessages((prev) => ([...prev, parsed]));


      }, contentHeader);

      setLoaded(true);

    }

    return () => { unsubscribe(); }
  }, [isConnected, subscribe, room]);




  const onChatSumbmit = (msg) => {
    sendMessage("/pub/chat/message", {
      type: 'TEXT_TALK',
      nickname: 'yun',
      email: user.email,
      roomId: room?.roomId,
      content: msg
    });


  }



  if (room) {




    //setMessages(roomMessages);





    return (
      // <div className="chat">
      //   <div className="top">
      <Chat>
        <Wrapper>
          <ChatHeader roomName={room.name} hideRoom={() => { }} />
          <ChatContent chattingList={messages}
            messageRef={messageRef} />
          <ChatFooter onChatSumbmit={onChatSumbmit} />
        </Wrapper>
      </Chat>
    )
  }
  else {

    return (
      <></>
    )
  }

}

export default memo(ChatArea);
