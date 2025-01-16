import React, { useState } from "react";
import DoneOutlineRoundedIcon from "@mui/icons-material/DoneOutlineRounded";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Box,
  TextField
} from "@mui/material";

import CloseIcon from "@mui/icons-material/Close";

import { useDispatch, useSelector } from "react-redux";
import { create } from "@mui/material/styles/createTransitions";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function CreateChatRoomDialog({
  roomName,  
  open,
  createChatRoom,
  handleRoomName,  
  handleDeleteRoomName,
  handleClose 
}
) {
  const lightTheme = useSelector((state) => state.themeKey);
  //const userData = JSON.parse(localStorage.getItem("userData"));
  // console.log("Data from LocalStorage : ", userData);
  const nav = useNavigate();
  // if (!userData) {
  //   console.log("User not Authenticated");
  //   nav("/");
  // }
  //const user = userData.data;
  
  
  
//  console.log("User Data from CreateGroups : ", userData);

  const createGroup = () => {
    // const config = {
    //   headers: {
    //     Authorization: `Bearer ${user.token}`,
    //   },
    // };

    // axios.post(
    //   "http://localhost:8080/chat/createGroup",
    //   {
    //     name: groupName,
    //     users: '["647d94aea97e40a17278c7e5","647d999e4c3dd7ca9a2e6543"]',
    //   },
    //   config
    // );
    //nav("/app/groups");
  };

  return (
    
      <div>
        <Dialog
          open={open}
          onClose={handleClose}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
        >
          <DialogTitle id="alert-dialog-title">
          {"create Room"}
          <IconButton onClick={handleClose}>
            <CloseIcon />
          </IconButton>
          </DialogTitle>
          <DialogContent>
            <Box display={"flex"} flexDirection={"column"} alignItems={"center"}>
            <TextField
              value={roomName}
              placeholder={"방이름"}
              onChange={handleRoomName}
            />
          </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Disagree</Button>
            <Button
              onClick={() => {
                createChatRoom();
                handleClose();
              }}
              autoFocus
            >
              Agree
            </Button>
          </DialogActions>
        </Dialog>
      </div>        
  );
}

export default CreateChatRoomDialog;
