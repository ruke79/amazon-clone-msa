import * as React from 'react';
import { styled } from '@mui/material/styles';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { Button } from '@mui/material';

const Item = styled(Button)(({ theme }) => ({
  backgroundColor: '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  textAlign: 'center',
  color: theme.palette.text.secondary,
  ...theme.applyStyles('dark', {
    backgroundColor: '#1A2027',
  }),
}));

const MicroServices = () => {
   
          
        
    return (
      <Box sx={{ flexGrow: 1 }}>
      <Grid container spacing={1}>
        <Grid size={8}>
          <Item href='chat'>Chat</Item>
        </Grid>        
      </Grid>
    </Box>
    );

   }

export default MicroServices;