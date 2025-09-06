import { useMutation, useQuery } from "@tanstack/react-query";
import { useRef, useEffect, useCallback  } from "react";
import { useDispatch } from "react-redux";
import { addRoomToList } from "../redux/ChatSlice";

import { api, createChatRoom, getChatRoomList, getRoomMessages } from "util/api";
import { useStomp } from "util/stompProvider";

export const useCreateChatRoom = (props) => {
    
    const dispatch = useDispatch();
    
    const {
      mutate : createChatRoomOp,
      data,
      isPending,
      isSuccess,
    } = useMutation({mutationFn : createChatRoom,
      onSuccess: (data) => {        
        if (data) {
          console.log(data);
          dispatch(addRoomToList(data));
        }
      },
      onError: (e) => {
        console.log(e);
      },
    });
    const handleCreateChatRoom = () => {
        createChatRoomOp(props);
    };
    return { handleCreateChatRoom, data, isPending, isSuccess };
  };

  export const useFetchChatRooms = () => {
   
    
    const { data, isPending, refetch, isSuccess, error, isError } = useQuery({
      queryKey : ["rooms"],
      queryFn : async () => {
        const res = await getChatRoomList();                  
        return res;
        
      },      
      throwOnError : true,      
  });
    
    
    return {
      roomList: data,
      isPending: isPending,
      isFetch: isSuccess,
      error : error, isError : isError,
      updateChatRoomList: refetch,
    };
  };

  export const useFetchRoomMessages = (room, cursor) => {
    
    
    
    const { data, isPending, refetch, isSuccess } = useQuery({
      queryKey : [`room_messages/${room?.roomId}`, cursor ],
      queryFn : async () => {

        const res = await getRoomMessages(room, cursor);        
        const messages = res?.reverse();
    
        return messages;
      },
    
      throwOnError : true,      
      staleTime : 1000,
      // roomId가 있을 때만 쿼리를 실행합니다.
      enabled: !!room?.roomId, 
  });
    
    return {
      roomMessages : data,
      isPending: isPending,
      isFetch: isSuccess,
      updateRoomMessages: refetch,
    };
  };
  
  // export const fetchMessages = (room, cursor) => {
    
  //   let promise = new Promise(async function (resolve, reject) {        
  //       const res = await getRoomMessages(room , cursor);              
  //       resolve(res);                         
  //     }
  //   );

  //   return promise;         
  // }


  export const useSendMessage = ({
    type,    
    roomId,
    sender,
    message,
    deleteMessage,
  } ) => {

    const { client } = useStomp();
    
    const sendMessage = useCallback(({ type, roomId, sender, message }) => {
    if (client) {
      client.send(
        "/pub/chat/message",
        { Authorization: api.defaults.headers.common["Authorization"] },
        JSON.stringify({ type, roomId, sender, message })
      );
    }
  }, [client]);

  return sendMessage;
  };

  export function usePrevState(state) {
    const ref = useRef(state);
    useEffect(() => {
      ref.current = state;
    }, [state]);
    return ref.current;
  }
  