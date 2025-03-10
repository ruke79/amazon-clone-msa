import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRef, useEffect  } from "react";
import { useDispatch } from "react-redux";
import { addRoomToList } from "../redux/ChatSlice";

import { createChatRoom, getChatRoomList, getRoomMessages } from "util/api";


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
      staleTime : 1000
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

  export function usePrevState(state) {
    const ref = useRef(state);
    useEffect(() => {
      ref.current = state;
    }, [state]);
    return ref.current;
  }
  