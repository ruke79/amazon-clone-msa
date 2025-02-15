import { useDispatch, useSelector } from 'react-redux';
import { selectRoom, setCursor } from '../../../redux/ChatSlice';
import { MainContent } from '../styles/BaseStyle';


const ChatRoom = (props) => {
    //const { name, status_msg, profile_img_url } = props;
    //const { profileImgClick, onDoubleClick } = props;  
    const dispatch = useDispatch();
    const { room,  } = props;  

    const updateSelectedRoom = () => {
       //dispatch(selectRoom(currentRoom));
    }
    
    const onDoubleClick = () => {
        dispatch(selectRoom(room));
        
    }

    return (      
        <li onDoubleClick={onDoubleClick} onClick={updateSelectedRoom}>
          {/* <img
            src={profile_img_url || BASE_IMG_URL}
            alt="profile Image"
            onClick={profileImgClick}
          /> */}
          <p>
            <b>{room.name}</b>
          </p>
          <p>{"Online"}</p>
        </li>
      );
}

export default ChatRoom;