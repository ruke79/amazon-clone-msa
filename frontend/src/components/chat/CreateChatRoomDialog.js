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
  const nav = useNavigate();
  
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
