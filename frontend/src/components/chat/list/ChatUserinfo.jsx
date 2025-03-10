import { useAuthContext } from "store/AuthContext"

import styled from "styled-components";

const Wrapper = styled.div`

  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height : 8vh;
  

  .user {
    display: flex;
    align-items: center;
    gap: 20px;

    img {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      object-fit: cover;
    }
  }

  .icons {
    display: flex;
    gap: 20px;

    img {
      width: 20px;
      height: 20px;
      cursor: pointer;
    }
  }
`;


const ChatUserinfo = () => {

  const { user } = useAuthContext();

  return (
    <Wrapper>
      <div className={"user"}>        
        <h2>{user.email}</h2> 
        </div>
      </Wrapper>
  )
}

export default ChatUserinfo