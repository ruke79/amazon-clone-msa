import { Box, Paper, Stack, List } from "@mui/material";


import ChatArea from "components/chat/ChatArea";
//import { MessageSearch } from "./MessageSearch";

import { Outlet, Route,  Routes } from "react-router-dom";


 export const ChatPage =() =>  {


    return (
        <Paper
      sx={{
        flex: 1,
        width: '100%',
        mx: 'auto',
        pt: { xs: 'var(--Header-height)', md: 0 },
        display: 'grid',
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'minmax(min-content, min(30%, 400px)) 1fr',
        },
      }}
    >
      <Paper
        sx={{
          position: { xs: 'fixed', sm: 'sticky' },
          transform: {
            xs: 'translateX(calc(100% * (var(--MessagesPane-slideIn, 0) - 1)))',
            sm: 'none',
          },
          transition: 'transform 0.4s, width 0.4s',
          zIndex: 100,
          width: '100%',
          top: 52,
        }}
      >
        {/* <MenuItemPage/> */}
        <Outlet/>

        
      </Paper>
      <ChatArea/>
      </Paper>     

    );

};

