import React ,  { useEffect, useState } from "react";
import { Box, Popover, InputBase, Typography, IconButton, Badge } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { useDispatch, useSelector } from "react-redux";
import { NavLink } from "react-router-dom";
import Input from '@mui/material/Input';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton, { listItemButtonClasses } from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import SidebarHeader from "./SidebarHeader";
import CreateChatRoomDialog from '../CreateChatRoomDialog';
import { useCreateChatRoom } from "hook/chatHooks";

const Sidebar = () => {
  const [open, setOpen] = useState(false);
  const [roomName, setRoomName] = useState("");

  const theme = useTheme();
  const handleRoomName = (e) => {
      setRoomName(e.target.value);
    };
  
    const handleDeleteRoomName = () => {
      setRoomName("");
    };
    
      const handleOpen = () => {
        setOpen(true);
      };
    
      const handleClose = () => {
        setOpen(false);
      };
  
      const { createChatRoom } = useCreateChatRoom({name : roomName})

  return (
    <Box
      sx={{
        width: { md: "360px", xs: "100vw", sm: "360px" },
        height: "100%",
        display: "flex",
        flexDirection: "column",
        backgroundColor: theme.palette.background.paper,
        color: theme.palette.text.primary,
        overflow: "hidden",
        boxShadow: theme.shadows[3],
        "@media screen and (max-width: 767px)": {
          height: "100%",
          position: "absolute",
          backgroundColor: theme.palette.background.paper,
          left: 0,
          top: 0,
          zIndex: 3,
        },
        "@media screen and (max-width: 1200px)": {
          ".chat-box-title": {
            fontSize: "20px",
          },
        },
        "@media screen and (min-width: 0px)": {
          ".title-root": {
            fontSize: "14px",
          },
          ".star-icon-root": {
            fontSize: "18px",
          },
        },
      }}
    >
         <SidebarHeader />
         <List
          size="small"
          sx={{
            gap: 1,
            '--List-nestedInsetStart': '30px',            
          }}
        >
          <ListItem >
            <ListItemButton onClick={handleOpen}>
              {/* <HomeRoundedIcon /> */}
              <ListItemText>
                <Typography level="title-sm">Home</Typography>                
              </ListItemText>
            </ListItemButton>            
          </ListItem>
          <ListItem component={NavLink} to='/chat/chatrooms'>
            <ListItemButton>
              {/* <DashboardRoundedIcon /> */}
              <ListItemText>
                <Typography level="title-sm">Search Message</Typography>
              </ListItemText>
            </ListItemButton>
          </ListItem>          
        </List>          
        {/* <Divider /> */}
        <CreateChatRoomDialog  
      roomName={roomName}
        open={open}
        createChatRoom={createChatRoom}
        handleRoomName={handleRoomName}
        handleClose={handleClose}        
        handleDeleteRoomName={handleDeleteRoomName}
      />  
      </Box>
  )
}

export default Sidebar;