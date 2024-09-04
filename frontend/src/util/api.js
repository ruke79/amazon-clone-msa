import axios from "axios";
import { QueryClient } from "@tanstack/react-query";
import toast from 'react-hot-toast';
import TokenUtil from "./tokenUtil"

export const queryClient = new QueryClient(
  {
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

// Add a request interceptor to include JWT and CSRF tokens
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

const config = {
  timeout: 10000,
  statusRange: function (status) {
    return status >= 200 && status < 500; // Resolve only if the status code is less than 500
  }
}

export function getRequest(URL, contentType = "application/json") {
  return api.get(`/${URL}`, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }).then(response => response);
}

export function postRequest(URL, payload, contentType = "application/json") {
  return api.post(`/${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }
  ).then(response => response);
}

export function patchRequest(URL, payload, contentType = "application/json") {
  return api.patch(`/${URL}`, payload, contentType,
    {
      timeout: config.timeout, // 10 seconds timeout
      validateStatus: config.statusRange
    }).then(response => response);
}

export function deleteRequest(URL, contentType = "application/json") {
  return api.delete(`/${URL}`, contentType,
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

    const { data } = await api.put("/user/cart/savecart", {
      products: cart
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
};

export const saveShippingAddress = async (address) => {

  try {



    const { data } = await api.post("/user/cart/save_shipping_address", {
      address: address
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const selectShippingAddress = async (addressId) => {

  try {



    // const params = new URLSearchParams();    
    // params.append('addressId', id);

    const { data } = await api.get(`/user/cart/selectaddress/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export const deleteAddress = async (addressId) => {

  try {

    const { data } = await api.get(`/user/cart/deleteaddress/${addressId}`

    );
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}



export const applyCoupon = async (coupon) => {

  try {

    const { data } = await api.post("/user/cart/coupon", {
      coupon: coupon
    });
    return data;
  } catch (error) {
    console.log("erorr >>>", error.response.data.message);
  }
}

export default api;

