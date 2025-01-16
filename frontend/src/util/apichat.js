import axios from "axios";

export const createChatRoom = ({ name }) => {
    const url = `/chat/createroom`;
    return axios.post(url, { name }).then((res) => {
      return res.data;
    });
  };
  
  export const getChatRoomList = () => {
    const url = `chat/chatrooms`;
    return axios.get(url).then((res) => {
      return res.data;
    });
  };
  