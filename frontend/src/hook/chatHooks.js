import { useMutation, useQuery } from "@tanstack/react-query";
import { useAuthContext } from "store/AuthContext";

import { useChatState } from "store/ChatContext";


import { createChatRoom, getChatRoomList } from "util/apichat";

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
  