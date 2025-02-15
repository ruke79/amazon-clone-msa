import React, { MouseEvent } from 'react';
import styled from 'styled-components';
const Wrapper = styled.header`
  width: 100%;
  background-color: #a9bdce;
  height: 50px;
  
  display: flex;
   align-items: center;  
  border-bottom: 1px solid #dddddd35;
  
  
  
  & span {
    display: inline-block;
    font-weight: bold;
    font-size: 20px;
    margin-left: 10px;
    margin-top: 10px;
  }
  & button {
    font-size: 20px;
    padding: 10px 10px 10px 30px;
    background-color: #a9bdce;
    outline: none;
    cursor: pointer;
    &:hover {
      color: #dcdcdc;
    }
  }
`;

const ChatHeader = props => {
    const { roomName, hideRoom } = props;
  const onBackBtnClick = (event) => {
    event.preventDefault();
    hideRoom();
  };
  return  (
    <Wrapper>
      <button type="button" onClick={onBackBtnClick}>
        <i className="fas fa-arrow-left" />
      </button>
      <span>{roomName}</span>
    </Wrapper>
  );

}

export default ChatHeader;