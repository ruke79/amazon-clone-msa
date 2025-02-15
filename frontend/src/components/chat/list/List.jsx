import { Outlet } from "@mui/icons-material";
import ChatRoomList from "./ChatRoomList"
import "./list.css"
import Userinfo from "./userInfo/Userinfo"
import styled from "styled-components";

const Wrapper = styled.div`

flex: 1;
display: flex;
flex-direction: column;    
//overflow : auto;  
`;



const List = () => {
  return (
    <Wrapper>
      <Userinfo/>
      {/* <ChatRoomList/> */}
      <Outlet/>
    </Wrapper>
  )
}

export default List