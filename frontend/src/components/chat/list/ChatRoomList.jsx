import React,{ useEffect, useState } from "react";

import RoomListHeader from "../room/RoomListHeader";
import RoomContent from  "../room/RoomContent";
import { useFetchChatRooms } from "hook/chatHooks";
import { useDispatch, useSelector } from "react-redux";
import { setRooms } from "../../../redux/ChatSlice";
import { rooms } from "../../../redux/ChatSelectors";
import DotLoaderSpinner from "components/loader/Loading";



const ChatRoomList = () => {
  const dispatch = useDispatch();  
  const [input, setInput] = useState("");
  const currRooms = useSelector(rooms);
  

  
  const { roomList, isPending, isFetch  } =  useFetchChatRooms();   
        
  useEffect(() => {        

    if (isFetch)   
      dispatch(setRooms(roomList))    

  }, [dispatch, , isPending] )  



  if (isPending) return; //<DotLoaderSpinner loading={isPending}/>;

  


  return (        
    <React.Fragment>
      <RoomListHeader changeSearch={setInput} />                  
       { roomList ? <RoomContent search={input} rooms={currRooms} />            
       : <div></div>
       }
     </React.Fragment>
  );
};

export default ChatRoomList;
