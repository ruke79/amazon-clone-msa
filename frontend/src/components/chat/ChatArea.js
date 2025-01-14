import React, { useContext, useEffect, useRef, useState } from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import { IconButton } from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import { Box, Paper, Stack, List } from "@mui/material";

//import MessageSelf from "./MessageSelf";
//import MessageOthers from "./MessageOthers";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import Skeleton from "@mui/material/Skeleton";
import axios from "axios";

import { ChatInput } from "./ChatInput";

export function ChatArea() {
  const lightTheme = useSelector((state) => state.themeKey);
  const [messageContent, setMessageContent] = useState("");
  const messagesEndRef = useRef(null);
  //const dyParams = useParams();
  //const [chat_id, chat_user] = dyParams._id.split("&");
  // console.log(chat_id, chat_user);
  const userData = JSON.parse(localStorage.getItem("userData"));
  const [allMessages, setAllMessages] = useState([]);
  // console.log("Chat area id : ", chat_id._id);
  // const refresh = useSelector((state) => state.refreshKey);
  // const { refresh, setRefresh } = useContext(myContext);
  const [loaded, setloaded] = useState(false);
  // const sendMessage = () => {
  //   // console.log("SendMessage Fired to", chat_id._id);
  //   const config = {
  //     headers: {
  //       Authorization: `Bearer ${userData.data.token}`,
  //     },
  //   };
  //   axios
  //     .post(
  //       "http://localhost:8080/message/",
  //       {
  //         content: messageContent,
  //         chatId: chat_id,
  //       },
  //       config
  //     )
  //     .then(({ data }) => {
  //       console.log("Message Fired");
  //     });
  // };
  // const scrollToBottom = () => {
  //   messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  // };

  // useEffect(() => {
  //   console.log("Users refreshed");
  //   const config = {
  //     headers: {
  //       Authorization: `Bearer ${userData.data.token}`,
  //     },
  //   };
  //   axios
  //     .get("http://localhost:8080/message/" + chat_id, config)
  //     .then(({ data }) => {
  //       setAllMessages(data);
  //       setloaded(true);
  //       // console.log("Data from Acess Chat API ", data);
  //     });
  //   // scrollToBottom();
  // }, [refresh, chat_id, userData.data.token]);
   return (
    <Paper
    sx={{
      height: { xs: 'calc(100dvh - var(--Header-height))', md: '100dvh' },
      display: 'flex',
      flexDirection: 'column',
      backgroundColor: 'background.level1',
    }}
   >
     <Box
        sx={{
          display: 'flex',
          flex: 1,
          minHeight: 0,
          px: 2,
          py: 3,
          overflowY: 'scroll',
          flexDirection: 'column-reverse',
        }}
      >
        <Stack spacing={2} sx={{ justifyContent: 'flex-end' }}>

        </Stack>
        </Box>
        <ChatInput />
      </Paper>

   ); 
  
}

export default ChatArea;
