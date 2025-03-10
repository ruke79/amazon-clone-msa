import { useDispatch} from 'react-redux';
import { selectRoom, showRoom } from '../../../redux/ChatSlice';
import { useState } from 'react';




const ChatRoom = (props) => {

    const dispatch = useDispatch();
    const { room,  } = props;  
    
    const onDoubleClick = () => {
            
        dispatch(selectRoom(room));          
        dispatch(showRoom(true));
        
    }

    return (      
        <li onDoubleClick={onDoubleClick} >          
          <p>
            <b>{room.name}</b>
          </p>
          <p>{"Online"}</p>
        </li>
      );
}

export default ChatRoom;