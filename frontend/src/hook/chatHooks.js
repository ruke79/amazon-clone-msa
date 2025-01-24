import { useMutation, useQuery } from "@tanstack/react-query";
import { useAuthContext } from "store/AuthContext";

import { useChatState } from "store/ChatContext";


import { createChatRoom, getChatRoomList, getRoomMessages } from "util/apichat";

export const useCreateChatRoom = (props) => {
    const chatContext = useChatState();
    const {user} = useAuthContext();
    
    const {
      mutate : createChatRoomOp,
      data,
      isLoading,
      isSuccess,
    } = useMutation({mutationFn : createChatRoom,
      onSuccess: (data) => {
        console.log(data)
        if (data) {
          console.log(data);
          chatContext.rooms = chatContext.rooms.concat(data);
        }
      },
      onError: (e) => {
        console.log(e);
      },
    });
    const handleCreateChatRoom = () => {
        createChatRoomOp(props);
    };
    return { handleCreateChatRoom, data, isLoading, isSuccess };
  };

  export const useFetchChatRooms = () => {
    const user = useChatState();
    const { data, isLoading, refetch, isSuccess } = useQuery(
      `rooms`,
      async () => {
        const res = await getChatRoomList();
        const chatRoomList = res;
        return chatRoomList;
      }
    );
    
    return {
      chatRoomList: data,
      isLoadingRoom: isLoading,
      isFetch: isSuccess,
      updateChatRoomList: refetch,
    };
  };

  export const useFetchRoomMessages = () => {
    const user = useChatState();
    const { data, isLoading, refetch, isSuccess } = useQuery(
      `room_messages`,
      async () => {
        const res = await getRoomMessages();
        const messages = res;
        return messages;
      }
    );
    
    return {
      chatRoomList: data,
      isLoadingRoom: isLoading,
      isFetch: isSuccess,
      updateRoomMessages: refetch,
    };
  };
  

  export const useSendMessage = ({
    type,
    client,
    roomId,
    sender,
    message,
    deleteMessage,
  } ) => {
    
    client.send(
      "/pub/chat/message",
      {
        Authorization: axios.defaults.headers.common["Authorization"],
      },
      JSON.stringify({
        type: type,
        roomId: roomId,
        sender: sender,
        message: message,
      })
    );
    deleteMessage();
  };