import styled from 'styled-components';
import { MainContent } from '../styles/BaseStyle';
import { useDispatch, useSelector } from 'react-redux';
import { selectedRoom } from '../../../redux/ChatSelectors';
import { selectRoom } from '../../../redux/ChatSlice';
import ChatRoom from './ChatRoom';


const RoomContent = ({search, rooms}) => {

    const searchRemoveBlank = search.replace(/ /g, '');
    const reg_exp = new RegExp(`^.*${searchRemoveBlank}.*$`);
    const temp = [...rooms];
    const roomList = temp.sort((a, b) => {
      return a.name.localeCompare(b.name);
    });

    const searchedRooms = roomList.filter(room => {
      return room.name.replace(/ /g, '').match(reg_exp);
    });

    const roomSelected = useSelector(selectedRoom);    
    const dispatch = useDispatch();
  
      
    
  

    return (      
        <MainContent>          
          <ul> {
            searchedRooms.map(room => {
              return (
                <ChatRoom room={room}               
                key = {room.roomId}                  
                />                
              )
              
          })
        }
        </ul>        
        </MainContent>      
    )   
        
}

export default RoomContent;