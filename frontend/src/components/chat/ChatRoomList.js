
import { useState } from "react";

import { Paper, List, Box } from '@mui/material';
import { ChatRoomListItem } from './ChatRoomListItem';
import { useChatState } from "store/ChatContext";
import SockJS from "sockjs-client";

export const ChatRoomList = (props) => {

  const user = useChatState();
  const [chatName, setChatName] = useState("");
  const [roomId, setRoomId] = useState("");
  const [isChat, setIsChat] = useState(false);



  const handleConnect = (roomId, roomName) => {
    client.current = Stomp.over(() => {
      const sock = new SockJS(process.env.PUBLIC_URL + '/chat');
      return sock;
    });
    setChatMessageList([]);
    client.current.connect(
      {
        Authorization: token,
      },
      () => {
        // (messageList: IChatDetail[]) => {

        client.current.subscribe(
          `/sub/chat/room/${roomId}`,
          (message) => {
            setChatMessage(JSON.parse(message.body));
          },
        );
      }
    );

    setChatName(roomName);
    setRoomId(roomId);
    setIsChat(true);
  };


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
        {user &&
          user.rooms.map((room) => {
            return (
              <ChatRoomListItem
                roomName={room.roomName}
                roomId={room.roomId}
                selected={roomId}
                handleConnect={handleConnect}
              />
            );
          })}        
    </List>
    </Paper>
  );

}