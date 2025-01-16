import { useMutation, useQuery } from "@tanstack/react-query";

import { useChatState } from "store/ChatContext";

import { createChatRoom, getChatRoomList } from "util/apichat";

export const useCreateChatRoom = (props) => {
    const user = useChatState();
    
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
          user.rooms = user.rooms.concat(data);
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
  