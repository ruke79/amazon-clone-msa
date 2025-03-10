import React, { useState, ChangeEvent, FormEvent } from 'react';
import styled from 'styled-components';
import Modal, { ModalProps } from '../Modal';
//import FoundFriendProfile from './FoundFriendProfile';
import { useCreateChatRoom, useFetchChatRooms } from 'hook/chatHooks';

const Wrapper = styled.div`
  width: 360px;
  height: 450px;
  border: 1px solid #646464;
  margin: auto;
  color: #000;
  background: #fff;
  & h4 {
    padding: 25px 20px;
    font-size: 18px;
    font-weight: 600;
  }

  & form {
    width: 90%;
    border-bottom: 2px solid #000;
    margin: 30px auto;
    & input,
    span {
      padding: 5px;
    }
    & input {
      width: 85%;
      outline: none;
      border: none;
    }
    & span {
      display: inline-block;
      width: 15%;
      text-align: center;
    }
  }
`;
const Menu = styled.div`
  padding: 0 20px;
  border-bottom: 1px solid #dcdcdc;
  & span {
    display: inline-block;
    font-size: 13px;
    font-weight: bold;
    border-bottom: 1px solid #000;
    padding: 10px 0;
  }
`;
const CancelIcon = styled.i`
  position: absolute;
  top: 15px;
  right: 15px;
  font-size: 15px;
  color: #000;
  z-index: 100;
  cursor: pointer;
`;

const FooterWrapper = styled.div`
  position: fixed;
  bottom: 0px;
  left: 0px;
  width: 100%;
  height: 80px;
  border-top: 1px solid #dcdcdc;
  text-align: right;
  & button {
    position: relative;
    transform: translateY(50%);
    border: 1px solid #dcdcdc;
    background: #fff;
    padding: 10px 25px;
    margin-right: 10px;
    cursor: pointer;
    &.confirm {
      background: #fee500;
      &:hover {
        background: #fada0a;
      }
    }
    &.disabled {
      color: #969696;
      background: #e2e2e2;
      pointer-events: none;
    }
    &.cancel {
      &:hover {
        background: #f5f5f5;
      }
    }
  }
`;

const Footer = props => {
  const { isCanSubmit, onConfirmed, onClose } = props;
  const buttonClassName = isCanSubmit ? 'confirm' : 'disabled';
  return (
    <FooterWrapper>
      <button className={buttonClassName} onClick={onConfirmed}>
        확인
      </button>
      <button className="cancel" onClick={onClose}>
        취소
      </button>
    </FooterWrapper>
  );
};


// 친구를 찾는 창
const CreateRoomModal = props => {
  const { overlayClose, onClose,   createRoom } = props;
  const MAX_LEN = 20;
  const [roomName, setRoomName] = useState('');
  const { handleCreateChatRoom} = useCreateChatRoom({roomName : roomName});
  const { updateChatRoomList } = useFetchChatRooms();
  
  const onIdInputChange = (event) => {
    event.preventDefault();
    const value = event.target.value;
    if (value.length <= 20) {
      setRoomName(event.target.value);
      
    }
    if (value.length === 0) {
      //setFoundUser(undefined);
    }
  };
  const onSubmit = async (event) => {
    event.preventDefault();
    // const user = await findUser(userId);
    // await setFoundUser(user);
    // await setFindUserId(userId);
  };

  const onConfirmed = () => {    
    handleCreateChatRoom(roomName);

    updateChatRoomList();

    onClose();
  }

  return (
    <Modal overlayClose={overlayClose} onClose={onClose}>
      <Wrapper>
        <CancelIcon className="fas fa-times" onClick={onClose} />
        <h4>Add Room</h4>
        <Menu>
          <span>Name</span>
        </Menu>
        <form onSubmit={onSubmit}>
          <input
            value={roomName}
            maxLength={MAX_LEN}
            autoFocus={true}
            onChange={onIdInputChange}
          />
          {/* <span>{`${userId.length}/${MAX_LEN}`}</span> */}
        </form>                
      </Wrapper>
      <Footer
          isCanSubmit={true}
          onConfirmed={onConfirmed}
          onClose={onClose}
        />
    </Modal>
  );
};

export default CreateRoomModal;
