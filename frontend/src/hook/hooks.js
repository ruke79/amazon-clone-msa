
import { useMutation, useQuery, } from "@tanstack/react-query";
import { createChatRoom, getChatRoomList, getRoomMessages, getReviews } from "util/api";
import { getRequest, getAddresses } from "util/api";

export const useFetchReviews = (productId) => {
    
    const { data, isLoading, refetch, isSuccess } = useQuery({
      queryKey : [`reviews/${productId}` ],
      queryFn : () => {
        const res = getReviews(productId);        
        console.log(res);
        return res;
      },      
      throwOnError : true,            
  });
    
    return {
      reviews : data,
      isLoading: isLoading,
      isSuccess: isSuccess,
      updateFetchReviews: refetch,
    };
  };

  export const useFetchAddresses = (userId) => { 

    const { data, isLoading, isSuccess } = useQuery({
      queryKey : [userId],
      queryFn :  () => {
        const res =  getAddresses();
        console.log(res);
        return res;
      },      
      throwOnError : true,            
  });
    
    return {
      data,
      isLoading,
      isSuccess,      
    };
};