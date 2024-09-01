import axios from "axios";
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


const refreshAndRetryQueue = [];

// Add a request interceptor to include JWT and CSRF tokens
api.interceptors.request.use(
  async (config) => {
    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    console.log(token);

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

let isRefreshing = false;

api.interceptors.response.use(
  (res) => {
    return res;
  },
  async (err) => {
    let originalConfig = err.config;
    const msg = err.response.data
    const status = err.response.status;

            
    
    //if (originalConfig.url !== "/auth/public/signin" && status) {
      // Access Token was expired
      if (status === 401 && err.response) {

        if (!isRefreshing) {

          console.log(msg);
          
          isRefreshing = true;
          try {
            

            if (msg === "access token expired") {

              

              
              const rs = await api.post("/cookie/refresh");
    
              if (rs.ok) {
                console.log(rs);
                const accessToken = rs.headers['access'];
                localStorage.setItem('access_token', accessToken);
                originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;
              } 
              else {
                localStorage.clear();      
                
                
              }

              console.log(rs);

              if (msg === "refresh token expired") {
                 localStorage.clear();               
              }
              
              refreshAndRetryQueue.forEach(({ config, resolve, reject }) => {
                api
                  .request(config)
                  .then((response) => resolve(response))
                  .catch((err) => reject(err));
              });

              refreshAndRetryQueue.length = 0;
    
              //window.location.reload();
              return api(originalConfig);
            }
          } catch (refreshError) {
            // Handle token refresh error
            // You can clear all storage and redirect the user to the login page
            console.log("error");
            
            throw refreshError;
          } finally {
            console.log("finally");
            isRefreshing = false;
          }
        }

         // Add the original request to the queue
      return new Promise((resolve, reject) => {
        refreshAndRetryQueue.push({ config: originalConfig, resolve, reject });
      });
    }
            

     

        
        
        return Promise.reject(err);
      }
   //}
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

