import React, { useContext, useEffect, useRef, useState } from "react";
import DeleteIcon from "@mui/icons-material/Delete";
import { IconButton } from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import { Box, Paper, Stack, List } from "@mui/material";

import { useDispatch, useSelector } from "react-redux";
import { useTheme } from "@mui/material/styles";

import Skeleton from "@mui/material/Skeleton";
import axios from "axios";
import RoomContentHeader from "./RoomContentHeader";
import ChatFooter from "./ChatFooter";
import RoomConversation from "./RoomConversation";
import {
  selectedRoom
} from "../../redux/ChatSelectors"



export function ChatArea() {

  const dispatch = useDispatch();

  const room = useSelector(selectedRoom);
  const theme = useTheme();
  

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        backgroundColor: theme.palette.background.default,
        width: "100%",
        height: "100%",
        transition: "all 0.3s ease",
        zIndex: 999,
      }}
    >
      {room && (
        <>
          <RoomContentHeader room={room} />
        </>
      )}
 
    </Box>
    )
}

export default ChatArea;
