
import {Paper, List, Box } from '@mui/material';
import { ChatRoomListItem } from './ChatRoomListItem';

export const ChatRoomList =() =>  {


    return (
        <Paper
        sx={{
          borderRight: '1px solid',
          borderColor: 'divider',
          height: { sm: 'calc(100dvh - var(--Header-height))', md: '100dvh' },
          overflowY: 'auto',
        }}
        >
        <List
        sx={{
          py: 0,
          '--ListItem-paddingY': '0.75rem',
          '--ListItem-paddingX': '1rem',
        }}
    >
        <ChatRoomListItem 
             roomName = {"chatroom"}
             selected = {true}
             roomId = {0}
        />
    </List>
    </Paper>
    );

}