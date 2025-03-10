import { useEffect, useState } from "react";

import RoomListHeader from "../room/RoomListHeader";
import RoomContent from  "../room/RoomContent";
import { useFetchChatRooms } from "hook/chatHooks";
import { useDispatch, useSelector } from "react-redux";
import { setRooms } from "../../../redux/ChatSlice";
import { rooms } from "../../../redux/ChatSelectors";



const ChatRoomList = () => {
  const dispatch = useDispatch();  
  const [input, setInput] = useState("");
  const currRooms = useSelector(rooms);
  

  
  const { roomList, isPending, isFetch, error, isError } =  useFetchChatRooms();   
        
  useEffect(() => {        

    if (isFetch)   
      dispatch(setRooms(roomList))    

  }, [dispatch, , roomList] )  

  if (isPending) return "Loading";


  return (        
    <div>
      <RoomListHeader changeSearch={setInput} />                  
       { roomList ? <RoomContent search={input} rooms={currRooms} />            
       : <div></div>
       }
     </div>
  );
};

export default ChatRoomList;
