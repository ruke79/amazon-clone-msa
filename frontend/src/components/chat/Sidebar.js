 import React, { useContext, useEffect, useState } from "react";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";

import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import ShoppingCartRoundedIcon from '@mui/icons-material/ShoppingCartRounded';
import AssignmentRoundedIcon from '@mui/icons-material/AssignmentRounded';
import QuestionAnswerRoundedIcon from '@mui/icons-material/QuestionAnswerRounded';
import GroupRoundedIcon from '@mui/icons-material/GroupRounded';
import SupportRoundedIcon from '@mui/icons-material/SupportRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import BrightnessAutoRoundedIcon from '@mui/icons-material/BrightnessAutoRounded';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import IconButton from '@mui/material/IconButton';

import { NavLink, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import GlobalStyles from '@mui/material/GlobalStyles';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Input from '@mui/material/Input';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton, { listItemButtonClasses } from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import CreateChatRoomDialog from './CreateChatRoomDialog';


function Sidebar() {
  
  // const dispatch = useDispatch();
  // const lightTheme = useSelector((state) => state.themeKey);
  // const refresh = useSelector((state) => state.refreshKey);
  // const { refresh, setRefresh } = useContext(myContext);
  //console.log("Context API : refresh : ", refresh);
  const [conversations, setConversations] = useState([]);
  // console.log("Conversations of Sidebar : ", conversations);
  const userData = JSON.parse(localStorage.getItem("userData"));
  // console.log("Data from LocalStorage : ", userData);
  const [open, setOpen] = React.useState(false);

  const [roomName, setRoomName] = useState("");
  
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


  
  // if (!userData) {
  //   console.log("User not Authenticated");
  //   nav("/");
  // }

  // const user = userData.data;
  // useEffect(() => {
  //   // console.log("Sidebar : ", user.token);
  //   const config = {
  //     headers: {
  //       Authorization: `Bearer ${user.token}`,
  //     },
  //   };

  //   axios.get("http://localhost:8080/chat/", config).then((response) => {
  //     console.log("Data refresh in sidebar ", response.data);
  //     setConversations(response.data);
  //     // setRefresh(!refresh);
  //   });
  // }, [refresh]);

  return (
    <Paper
    className="Sidebar"
    sx={{
      position: { xs: 'fixed', md: 'sticky' },
      transform: {
        xs: 'translateX(calc(100% * (var(--SideNavigation-slideIn, 0) - 1)))',
        md: 'none',
      },
      transition: 'transform 0.4s, width 0.4s',
      zIndex: 10000,
      height: '100dvh',
      width: 'var(--Sidebar-width)',
      top: 0,
      p: 2,
      flexShrink: 0,
      display: 'flex',
      flexDirection: 'column',
      gap: 2,
      borderRight: '1px solid',
      borderColor: 'divider',
    }}
  >
    <GlobalStyles
      styles={(theme) => ({
        ':root': {
          '--Sidebar-width': '220px',
          [theme.breakpoints.up('lg')]: {
            '--Sidebar-width': '240px',
          },
        },
      })}
    />
    <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
        <IconButton variant="soft" color="primary" size="sm">
          <BrightnessAutoRoundedIcon />
        </IconButton>
        <Typography level="title-lg">Acme Co.</Typography>        
      </Box>
      <Input size="sm" startDecorator={<SearchRoundedIcon />} placeholder="Search" />
      <Box
        sx={{
          minHeight: 0,
          overflow: 'hidden auto',
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          [`& .${listItemButtonClasses.root}`]: {
            gap: 1.5,
          },
        }}
      >
        <List
          size="sm"
          sx={{
            gap: 1,
            '--List-nestedInsetStart': '30px',            
          }}
        >
          <ListItem >
            <ListItemButton onClick={handleOpen}>
              <HomeRoundedIcon />
              <ListItemText>
                <Typography level="title-sm">Home</Typography>                
              </ListItemText>
            </ListItemButton>            
          </ListItem>
          <ListItem component={NavLink} to='/menu/searchmessage'>
            <ListItemButton>
              <DashboardRoundedIcon />
              <ListItemText>
                <Typography level="title-sm">Search Message</Typography>
              </ListItemText>
            </ListItemButton>
          </ListItem>          
        </List>          
          <Divider />
        </Box>                
        <CreateChatRoomDialog  
      roomName={roomName}
        open={open}
        handleRoomName={handleRoomName}
        handleClose={handleClose}        
        handleDeleteRoomName={handleDeleteRoomName}
      />  
     </Paper>      
    
  );
}

export default Sidebar;
