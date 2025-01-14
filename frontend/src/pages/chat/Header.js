
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import IconButton from '@mui/material/IconButton';
import GlobalStyles from '@mui/material/GlobalStyles';
import Paper from '@mui/material/Paper';

export default function Header() {
    return (
      <Paper
        sx={{
          display: { sm: 'flex', md: 'none' },
          alignItems: 'center',
          justifyContent: 'space-between',
          position: 'fixed',
          top: 0,
          width: '100vw',
          height: 'var(--Header-height)',
          zIndex: 9995,
          p: 2,
          gap: 1,
          borderBottom: '1px solid',
          borderColor: 'background.level1',
          boxShadow: 'sm',
        }}
      >
        <GlobalStyles
          styles={(theme) => ({
            ':root': {
              '--Header-height': '52px',
              [theme.breakpoints.up('lg')]: {
                '--Header-height': '0px',
              },
            },
          })}
        />        
      </Paper>
    );
  }