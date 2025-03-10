import axios from "axios";
import { QueryClient, QueryCache } from "@tanstack/react-query";
import toast from 'react-hot-toast';
import TokenUtil from "./tokenUtil"

export const queryClient = new QueryClient(
  {
    defaultOptions: {
      queries: {
        retry: false,
        throwOnError: true,
      },
      mutations: {
        throwOnError: true,
      },
    },
    queryCache: new QueryCache({
      onError: (error, query) => {
        console.log(error.message);
        if (query.state.data !== undefined) {
          toast.error(`Something went wrong: ${error}`);
        }
      },
    }),
  }
);




//console.log("API URL:", process.env.REACT_APP_API_URL);

// Create an Axios instance
const api = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});


// api.interceptors.request.use(
//   async (config) => {
    
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     }


//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

let isRefreshing = false;

const refreshAndRetryQueue = [];



// api.interceptors.response.use(
//   (res) => {
//     console.log(res.data)
//     return res;
//   },
//   async (err) => {
//     let originalConfig = err.config;

//     if (err.response) {

//       const msg = err.response.data;
//       const status = err.response.status;

      
//       if (status === 401) {


//         if (!isRefreshing) {

//           isRefreshing = true;


//           try {

//             if (msg === "access token expired") {

//               TokenUtil.removeToken();



//               const rs = await getRequest("user-service/api/token/refresh");

//               if (rs.status == 200) {
//                 console.log(rs);
//                 const accessToken = rs.headers['access'];
//                 TokenUtil.updateToken(accessToken);

//                 //setToken(accessToken);

//                 originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;

//                 return api(originalConfig);
//               }

//             }
//             refreshAndRetryQueue.forEach(({ config, resolve, reject }) => {
//               api
//                 .request(config)
//                 .then((response) => resolve(response))
//                 .catch((err) => reject(err));
//             });

//             refreshAndRetryQueue.length = 0;


//           } catch (refreshError) {
//             refreshAndRetryQueue.length = 0;

//             console.log(refreshError);
//             TokenUtil.remove();
//             throw refreshError;

//           } finally {
//             isRefreshing = false;
//           }
//         }


//         return new Promise((resolve, reject) => {
//           refreshAndRetryQueue.push({ config: originalConfig, resolve, reject });
//         });

//       }
//       else if (status === 400) {


//         if (msg === "refresh token expired") {

//           TokenUtil.remove();
//           //document.location.href = '/signin'

//         }
//       }
//     }
//     else {
//       // We have a network error
//       console.error('Network error:', err);
//       //navigate('error_server');
//       TokenUtil.remove();
//     }

//     return Promise.reject(err);

//   }
// );




const config = {
  timeout: 10000,
  statusRange: function (status) {
    return status >= 200 && status < 500; // Resolve only if the status code is less than 500
  }
}


export function getRequest(URL, payload = null, contentType = "application/json") {
  return api.get(`${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }
  ).then(response => response);
}

export function postRequest(URL, payload, contentType = "application/json") {
  return api.post(`${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }
  ).then(response => response);
}

export function putRequest(URL, payload, contentType = "application/json") {
  return api.put(`${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }
  ).then(response => response);
}

export function patchRequest(URL, payload, contentType = "application/json") {
  return api.patch(`${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }).then(response => response);
}

export function deleteRequest(URL, contentType = "application/json") {
  return api.delete(`${URL}`, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }).then(response => response);
}

const axiosRetry = (url, retries, delay) => {
  return api.get(url).catch(function (error) {
    if (retries > 0 && !error.response) {
      // Wait for delay amount before retrying
      setTimeout(() => {
        return axiosRetry(url, retries - 1, delay * 2); // Double the delay for exponential backoff
      }, delay);
    } else {
      return Promise.reject(error);
    }
  });
}

export const saveCart = async (products, email) => {
  try {
    

    const { data } = await postRequest("/cart-service/api/cart/savecart", { products : products, email : email });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
};

export const saveShippingAddress = async (address) => {

  try {

    const { data } = await postRequest("/user-service/api/user/profile/save_address", {
      address: address
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const selectShippingAddress = async (addressId) => {

  try {

    const { data } = await getRequest(`/user-service/api/user/profile/select/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const deleteAddress = async (addressId) => {

  try {

    const { data } = await deleteRequest(`/user-service/api/user/profile/delete/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}



export const applyCoupon = async (cartTotal, coupon, email) => {

  try {

    const { data } = await postRequest("/coupon-service/api/coupon/applycoupon", {
      totalPrice : cartTotal, couponName: coupon, userEmail : email });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const getOrders = async (filter,userEmail) => {

  try {
    
    const { data } = await getRequest("/order-service/api/order/orders", 
      { params : { filter : filter, email:userEmail }                                  
       } 
    );   
    
      return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const getOrder = async (orderId, userEmail) => {

    try {
      console.log(orderId);
    
      const { data } = await getRequest(`/order-service/api/order/${orderId}`,
        { params : { email : userEmail}}
      );
      
        return data;
    } catch (error) {
      console.log("erorr >>>", error.response.data.message);
    }
  };                       


export const loadCart = async (userEmail) => {

  try {
    
    const { data } = await getRequest("/cart-service/api/cart/loadcart", 
      { params : { userId : userEmail } }
    );
    
      return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const loadCheckOut = async (userEmail) => {

  
  try {
    
    const { data } = await getRequest("/cart-service/api/cart/checkout", 
      { params : {userId : userEmail } }
    );    
    console.log(data);
      return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

      





export const getCoupons = async (userEmail) => {

  try {

    
    const { data } = await getRequest("/coupon-service/api/coupon/coupons", { params : { email : userEmail } });    
    
      return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const getReviews = async (productId) => {
  try {
    
    const {data} = await getRequest(`/user-service/api/review/reviews/${productId}`);

    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const getAddresses = async() => {

  try {
    
    const {data} =   await getRequest("/user-service/api/user/profile/address");     
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const processPayment = async (request) => {

  try {
    
    const {data} =   await getRequest("/order-service/api/order/payment/process", request);     
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }

}

export const createChatRoom = ({ roomName }) => {
  const url = `/chat-service/chat/room`;
  return api.post(url, { roomName : roomName }).then((res) => {
    return res.data;
  });
};

export const  getChatRoomList = async () => {
  const url = `/chat-service/chat/rooms`;
  // return api.get(url).then((res) => {            
  //   return res.data;
  // }).catch((error)=> { console.log(error.response.data)});
  try {    
    const {data} =   await getRequest(url);     
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
};

export const getRoomMessages = async (room, cursor) => {
  const url = `/chat-service/chat/room/${room?.roomId}`;
  
  
  try {    
    const {data} =   await getRequest(url, { params : {cursor : cursor}});     
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
};


export default api;

