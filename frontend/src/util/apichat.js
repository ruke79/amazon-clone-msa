import axios from "axios";

const chatapi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}/chat-service/api`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});


export const createChatRoom = ({ id, name }) => {
    const url = `/chat/room`;
    return chatapi.post(url, { id, name }).then((res) => {
      return res.data;
    });
  };
  
  export const getChatRoomList = () => {
    const url = `chat/chatrooms`;
    return chatapi.get(url).then((res) => {
      return res.data;
    });
  };

  export const getRoomMessages = () => {
    const url = `chat/messages`;
    return chatapi.get(url).then((res) => {
      return res.data;
    });
  };
  