import { Box, Paper, InputBase, Stack, Input, List } from "@mui/material";

export const ChatInput = () => {


    return (
        <Box sx={{ px: 2, pb: 3 }}>

        <Paper
        component="form"
        sx={{
          p: "2px 4px",
          display: "flex",
          alignItems: "center",
          width: `95%`,
        }}
      >
        <InputBase
          sx={{ ml: 1, flex: 1 }}
          placeholder={"message"}
          inputProps={{ "aria-label": "search google maps" }}
        //   value={message}
        //   onChange={handleMessage}
        //   onKeyPress={pressEnter}
        />

        <InputBase disabled={true} />

        </Paper>

        </Box>

    );

}