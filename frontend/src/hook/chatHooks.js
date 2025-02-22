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
      isLoading,
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
    return { handleCreateChatRoom, data, isLoading, isSuccess };
  };

  export const useFetchChatRooms = () => {
   
    
    const { data, isLoading, refetch, isSuccess, error, isError } = useQuery({
      queryKey : ["rooms"],
      queryFn : async () => {
        const res = await getChatRoomList();          
        console.log(res)  
        return res;
      },            
      throwOnError : true,      
  });
    
    
    return {
      roomList: data,
      isLoading: isLoading,
      isFetch: isSuccess,
      error : error, isError : isError,
      updateChatRoomList: refetch,
    };
  };

  export const useFetchRoomMessages = (room, cursor) => {
    
    //const dispatch = useDispatch();
    const queryClient = useQueryClient();

    
    const { data, isLoading, refetch, isSuccess } = useQuery({
      queryKey : [`room_messages/${room?.roomId}`, cursor ],
      queryFn : async () => {

        const res = await getRoomMessages(room, cursor);        
        //console.log(res);
        const messages = res.reverse();
        //dispatch(setRoomConversation(messages));
        return messages;
      },
      //onSuccess: (data) => queryClient.invalidateQueries([`room_messages/${room?.roomId}`, cursor ]),
      throwOnError : true,      
      staleTime : 1000
  });
    
    return {
      roomMessages : data,
      isLoading: isLoading,
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
  