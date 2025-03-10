
import { useMutation, useQuery, } from "@tanstack/react-query";
import { createChatRoom, getChatRoomList, getRoomMessages, getReviews, getCoupons, getOrder } from "util/api";
import { getRequest, getAddresses, getOrders, loadCart, loadCheckOut } from "util/api";

export const useFetchReviews = (productId) => {
    
    const { data, isPending, refetch, isSuccess } = useQuery({
      queryKey : [`reviews/${productId}` ],
      queryFn : async () => {
        const res = await getReviews(productId);        
        console.log(res);
        return res;
      },      
      throwOnError : true,            
  });
    
    return {
      reviews : data,
      isPending: isPending,
      isSuccess: isSuccess,
      updateFetchReviews: refetch,
    };
  };

  export const useFetchAddresses = (userId) => { 

    const { data, isPending, isSuccess } = useQuery({
      queryKey : [`address/${userId}`],
      queryFn :  async () => {
        const res =  await getAddresses();
        console.log(res);
        return res;
      },      
      throwOnError : true,            
  });
    
    return {
      addressesData : data,
      isPendingAddress : isPending,
      isSuccessAddress : isSuccess,      
    };
};


export const useFetchCoupons = (email) => { 

  const { data, isPending, isSuccess } = useQuery({
    queryKey : [`coupons/${email}`],
    queryFn :  async () => {
      const res =  await getCoupons(email);
      console.log(res);
      return res;
    },      
    throwOnError : true,            
});
  
  return {
    coupons : data,
    isPending,
    isSuccess,      
  };
};

export const useFetchOrders = (filter, email) => { 

  const { data, isPending, isSuccess } = useQuery({
    queryKey : [`${email}/${filter}`],
    queryFn :  async () => {
      const res =  await getOrders(filter, email);      
      return res;
    },      
    throwOnError : true,            
});
  
  return {
    orders : data,
    isPending,
    isSuccess,      
  };
};

export const useFetchCart = (email) => { 

  const { data, isPending, isSuccess } = useQuery({
    queryKey : [`cart/${email}`],
    queryFn :  async () => {
      const res =  await loadCart(email);      
      return res;
    },      
    throwOnError : true,            
});
  
  return {
    cartData : data,
    isPendingCart : isPending,
    isSuccessCart : isSuccess,      
  };
};

export const useFetchCheckOut = (userId, email) => { 


    const { data, isPending, isSuccess, isError, status } = useQuery({
      queryKey : [`checkout/${email}`, userId],
      queryFn :  async () => {        
        const res =  await loadCheckOut(email);              
        console.log(res);
        return res;
      } ,            
      throwOnError : true,            
  });

  
  return {
    checkoutData : data,    
     isPendingCheckOut : isPending,
     isSuccessCheckOut : isSuccess,              
     status : status,
     isError
  };
};


export const useFetchOrder = (orderId, email) => { 


  const { data, isPending, isSuccess, isError, status } = useQuery({
    queryKey : [`order/${email}/${orderId}` ],
    queryFn :  async () => {        
      const res =  await getOrder(orderId, email);              
      console.log(res);
      return res;
    } ,          
    throwOnError : true,            
});


return {
   orderData : data,    
   isPendingOrder : isPending,
   isSuccessOrder : isSuccess,              
   status : status,
   isError
};
};


