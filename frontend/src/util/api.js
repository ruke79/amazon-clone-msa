import axios from "axios";
import TokenUtil from "./tokenUtil"
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

