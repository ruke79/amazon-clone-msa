import styled from 'styled-components';


const Chat = styled.div`
flex: 2;
border-left: 1px solidrgba(2, 2, 2, 0.21);
border-right: 1px solid #dddddd35;  
display: flex;
flex-direction: column;  
 width: 100%;
 height: 100vh;
background: #b2c7d9;  
`;

const EmptyChatArea = () => {

    return (        
        <Chat>          
        </Chat>
      );    
}

export default EmptyChatArea;