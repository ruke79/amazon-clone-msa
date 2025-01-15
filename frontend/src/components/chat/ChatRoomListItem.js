import * as React from 'react';

import {Box, Divider, ListItemButtonProps, ListItemButton, ListItem, Stack, Typography  } from '@mui/material';


export const ChatRoomListItem = ({
    roomName,
    selected,
    roomId,

}) => {


    return (
        <React.Fragment>
            <ListItem>
        <ListItemButton          
          selected={selected}
          color="neutral"
          sx={{ flexDirection: 'column', alignItems: 'initial', gap: 1 }}
        >
          <Stack direction="row" spacing={1.5}>
          <Box sx={{ flex: 1 }}>
              <Typography level="title-sm">roomname</Typography>
              <Typography level="body-sm">username</Typography>
            </Box>
          </Stack>
          </ListItemButton>
      </ListItem>
      <Divider sx={{ margin: 0 }} />
        </React.Fragment>  
            );

}