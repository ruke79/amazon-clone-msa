
import { useMutation, useQuery, useQueryClient, InfiniteQueryObserverResult } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import { createChatRoom, getChatRoomList, getRoomMessages, getReviews, getCoupons, getOrder } from "util/api";
import { getRequest, postRequest, getAddresses, getOrders, loadCart, loadCheckOut } from "util/api";
import tokenUtil from "util/tokenUtil";



export function useInfiniteScroll({ hasNextPage, fetchNextPage }) {

  const ref = useRef < HTMLDivElement > (null);

  const handleIntersect = useCallback(([entry], observer) => {
    if (entry?.isIntersecting && hasNextPage) {
      fetchNextPage();
    }
  }, [fetchNextPage, hasNextPage]);

  useEffect(() => {
    let observer;
    if (ref.current) {
      observer = new IntersectionObserver(handleIntersect, { threshold: 0.6, });
      observer.observe(ref.current);
    }
    return () => observer && observer.disconnect();
  }, [ref, handleIntersect]);

  return ref;

}

export const useLogout = () => {

  const navigate = useNavigate();
  const { logout } = useAuthContext();


  const handleLogout = async () => {

    try {
      await postRequest('/user-service/api/auth/logout', null);

      tokenUtil.remove();
      logout();
      navigate('/signin');

      toast.success("Signout successed. ");
    }
    catch (err) {
      toast.error("Logout failed. ");

    }

  };

  return () => handleLogout();

}


export const useFetchReviews = (productId) => {

  const { data, isPending, refetch, isSuccess } = useQuery({
    queryKey: [`reviews/${productId}`],
    queryFn: async () => {
      const res = await getReviews(productId);
      return res;
    },
    throwOnError: true,
  });

  return {
    reviews: data,
    isPending: isPending,
    isSuccess: isSuccess,
    updateFetchReviews: refetch,
  };
};

export const useFetchAddresses = (userId) => {

  const { data, isPending, isSuccess } = useQuery({
    queryKey: [`address/${userId}`],
    queryFn: async () => {
      const res = await getAddresses();
      return res;
    },
    throwOnError: true,
  });

  return {
    addressesData: data,
    isPendingAddress: isPending,
    isSuccessAddress: isSuccess,
  };
};


export const useFetchCoupons = (email) => {

  const { data, isPending, refetch, isSuccess } = useQuery({
    queryKey: [`coupons/${email}`],
    queryFn: async () => {
      const res = await getCoupons(email);
      return res;
    },
    throwOnError: true,
  });

  return {
    coupons: data,
    isPending,
    isSuccess,
    updateFetchCoupons: refetch
  };
};

export const useFetchOrders = (filter, email) => {

  const { data, isPending, refetch, isSuccess } = useQuery({
    queryKey: [`${email}/${filter}`],
    queryFn: async () => {
      const res = await getOrders(filter, email);
      return res;
    },
    throwOnError: true,
  });

  return {
    orders: data,
    isPending,
    isSuccess,
    updateFetchOrders: refetch,
  };
};

export const useFetchCart = (email, status, cartId) => {


  const { data, isPending, isSuccess, refetch } = useQuery({
    queryKey: [{ cartId: cartId, status: status }],
    queryFn: async () => {
      const res = await loadCart(email);
      console.log(res);
      return res;
    },
    throwOnError: true,
  });

  return {
    cartData: data,
    isPendingCart: isPending,
    isSuccessCart: isSuccess,
    updateCart: refetch,
  };
};

export const useFetchCheckOut = (email, cartId, cartStatus) => {


  const { data, isPending, isSuccess, refetch, isError, status } = useQuery({
    queryKey: [{ cartId: cartId, cartStatus: cartStatus }, `checkout/${email}`],
    queryFn: async () => {
      const res = await loadCheckOut(email);
      return res;
    },
    refetchOnMount: true,
    throwOnError: true,
  });


  return {
    checkoutData: data,
    isPendingCheckOut: isPending,
    isSuccessCheckOut: isSuccess,
    status: status,
    isError,
    updateFetchCheckOut: refetch
  };
};


export const useFetchOrder = (orderId, email) => {


  const { data, isPending, isSuccess, isError, status } = useQuery({
    queryKey: [`order/${email}/${orderId}`],
    queryFn: async () => {
      const res = await getOrder(orderId, email);
      return res;
    },
    throwOnError: true,
  });


  return {
    orderData: data,
    isPendingOrder: isPending,
    isSuccessOrder: isSuccess,
    status: status,
    isError
  };
};


