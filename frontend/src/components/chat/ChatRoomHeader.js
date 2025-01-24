import { Box, IconButton, Typography, useTheme } from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import KeyboardBackspaceIcon from "@mui/icons-material/KeyboardBackspace";
import Popover from "@mui/material/Popover";

import { v4 as uuid } from "uuid";


const ChatRoomHeader = ({ room }) => {

    const handleButtonClose = () => {
        //dispatch(onRoomSelect(null));
        //dispatch(leaveRoomSocket());
    };


    return (
        <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          padding: "16px",
          marginBottom: "16px",
          backgroundColor: "background.level1",
          justifyContent: "space-between",
        }}        
      >
        <IconButton onClick={handleButtonClose}>
        <KeyboardBackspaceIcon />
      </IconButton>
      <Box
          sx={{
            p: { xs: theme.spacing(4), md: theme.spacing(6) },
            display: "flex",
            flexDirection: "column",
          }}
          className="room-info"
        >
          <Typography
            variant="h6"
            sx={{
              fontSize: "22px",
              color: theme.palette.text.primary,
            }}
          >
            {/* {"# " + room.room_name} */}
          </Typography>
          <Typography
            sx={{
              fontSize: "12px",
              color: theme.palette.text.primary,
            }}
          >
            {/* {room.description} */}
          </Typography>
        </Box>      
      <Box ml="auto">
        {/* <DropdownMenu
          TriggerComponent={
            <IconButton>
              <MoreVertIcon />
            </IconButton>
          }
          items={actions}
          onItemClick={onOptionItemClick}
        /> */}
      </Box>
    </Box >
    )

}

export default ChatRoomHeader;