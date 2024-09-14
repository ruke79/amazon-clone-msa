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
        if (query.state.data !== undefined) {
          toast.error(`Something went wrong: ${error.message}`);
        }
      },
    }),
  }
);




console.log("API URL:", process.env.REACT_APP_API_URL);



// Create an Axios instance
const api = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}/api`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});


api.interceptors.request.use(
  async (config) => {
      const token = TokenUtil.getToken();
      if (token) {
          config.headers.Authorization = `Bearer ${token}`;
      }
    

      return config;
  },
  (error) => {
      return Promise.reject(error);
  }
);

let isRefreshing = false;

const refreshAndRetryQueue = [];



api.interceptors.response.use(
  (res) => {
      return res;
  },
  async (err) => {
      let originalConfig = err.config;
      
      if (err.response) {

          const msg = err.response.data;
          const status = err.response.status;

          console.log(msg);
       
      if (status === 401 ) {
       

          if (!isRefreshing) {

              isRefreshing = true;
              

              try {
                  
                  if (msg === "access token expired" ) {

                      TokenUtil.removeToken();

                      
                      
                          const rs = await postRequest("/token/refresh", null);

                          if( rs.status == 200) {
                                  console.log(rs);
                                  const accessToken = rs.headers['access'];
                                  TokenUtil.updateToken(accessToken);
                                  
                                  //setToken(accessToken);

                                  originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;

                              return api(originalConfig);
                          }                                                                   
                      
                  }
                  refreshAndRetryQueue.forEach(({ config, resolve, reject }) => {
                      api
                          .request(config)
                          .then((response) => resolve(response))
                          .catch((err) => reject(err));
                  });

                  refreshAndRetryQueue.length = 0;


              } catch (refreshError) {
                  refreshAndRetryQueue.length = 0;
                  
                  console.log(refreshError);
                  TokenUtil.remove();                            
                  //setToken(null) ;                            
                 // navigate('signin');    
                  throw refreshError;                                                                                                  
                  
              } finally {
                  isRefreshing = false;
              }
          }

         
            return new Promise((resolve, reject) => {
                refreshAndRetryQueue.push({ config: originalConfig, resolve, reject });
            });        

      }
      else if (status === 400) {

                
          if (msg === "refresh token expired") {

                      TokenUtil.remove();                                                                                                               
                                  
          }                
      }
  }
  else {
          // We have a network error
          console.error('Network error:', err);
          //navigate('error_server');
          TokenUtil.remove();                    
  }

      return Promise.reject(err);

  }                               
);




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

export const saveCart = async (cart) => {
  try {

    const { data } = await putRequest("/user/cart/savecart", {
      products: cart
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
};

export const saveShippingAddress = async (address) => {

  try {

    const { data } = await postRequest("/user/cart/save_shipping_address", {
      address: address
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const selectShippingAddress = async (addressId) => {

  try {

    const { data } = await getRequest(`/user/cart/selectaddress/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const deleteAddress = async (addressId) => {

  try {

    const { data } = await deleteRequest(`/user/cart/deleteaddress/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}



export const applyCoupon = async (coupon) => {

  try {

    const { data } = await postRequest("/user/cart/coupon", {
      coupon: coupon
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export default api;

