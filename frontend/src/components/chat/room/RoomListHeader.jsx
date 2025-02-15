import React, { useState, ChangeEvent } from 'react';
import { MainHeader, TitleBlock } from '../styles/BaseStyle';
import CreateRoomModal from './CreateRoomModal';


const RoomListHeader= ({ changeSearch, createRoom }) => {
  const [isOpenCreateRoom, setIsOpenCreateRoom] = useState(false);


  // 친구 찾기 창(modal)
  const showFindFriend = isOpenCreateRoom ? (
    <CreateRoomModal
      onClose={() => setIsOpenCreateRoom(false)}
      overlayClose={false}  
    />
  ) : null;

  const onSearchChange = (event) => {
    event.preventDefault();
    changeSearch(event.target.value);
  };
  return (
    <React.Fragment>
      {showFindFriend}
      <MainHeader>
        <TitleBlock>
          <h2>Room</h2>
          <i
            className="fas fa-user-plus"
            title="Add Room"
            onClick={() => setIsOpenCreateRoom(true)}
          />
        </TitleBlock>
        <input placeholder="Search Name" onChange={onSearchChange} />
      </MainHeader>
    </React.Fragment>
  );
};

export default RoomListHeader;
