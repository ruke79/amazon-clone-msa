import api from "./api";
import axios from "axios";
import TokenUtil from "./tokenUtil";

const chatapi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}/chat-service`,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

//chatapi.interceptors = api.interceptors;
chatapi.interceptors.request.use(
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


chatapi.interceptors.response.use(
  (res) => {    
    return res;
  },
  async (err) => {
    let originalConfig = err.config;

    if (err.response) {

      const msg = err.response.data;
      const status = err.response.status;

      
      if (status === 401) {


        if (!isRefreshing) {

          isRefreshing = true;


          try {

            if (msg === "access token expired") {

              TokenUtil.removeToken();



              const rs = await getRequest("/token/refresh");

              if (rs.status == 200) {
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
          //document.location.href = '/signin'

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

 


export const createChatRoom = ({ roomName }) => {
    const url = `/chat/room`;
    return chatapi.post(url, { roomName : roomName }).then((res) => {
      return res.data;
    });
  };
  
  export const  getChatRoomList = () => {
    const url = `/chat/rooms`;
    return chatapi.get(url).then((res) => {            
      return res.data;
    }).catch((error)=> { console.log(error.response.data)});
  };

  export const getRoomMessages = (room, cursor) => {
    const url = `/chat/room/${room?.roomId}`;
    console.log(cursor);
    return chatapi.get(url,  { params : {cursor : cursor} }).then((res) => {
      return res.data;
    });
  };
  