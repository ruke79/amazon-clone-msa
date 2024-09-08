import api, { postRequest } from "util/api";
import TokenUtil from "util/tokenUtil";
import { useAuthContext } from "store/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";



function ApiErrorHandler({ children }) {

   

    const { token, setToken, setIsAdmin, setCurrentUser } = useAuthContext();
    const navigate = useNavigate();

    useEffect(() => {

        let isRefreshing = false;

        const refreshAndRetryQueue = [];

        // Add a request interceptor to include JWT and CSRF tokens
        const requestInterceptor = api.interceptors.request.use(
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



        const responseIntercetpor = api.interceptors.response.use(
            (res) => {
                return res;
            },
            async (err) => {
                let originalConfig = err.config;
                
                if (err.response) {

                    const msg = err.response.data;
                    const status = err.response.status;

                if (status === 401 ) {

                    if (!isRefreshing) {

                        isRefreshing = true;
                        

                        try {
                            
                            if (msg === "access token expired" || msg === "inavlid access token") {

                                TokenUtil.removeToken();

                                
                                    const rs = await postRequest("/token/refresh", null);

                                    if( rs.status == 200) {
                                            console.log(rs);
                                            const accessToken = rs.headers['access'];
                                            TokenUtil.updateToken(accessToken);
                                            
                                            setToken(accessToken);

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
                            
                            TokenUtil.remove();                            
                            //setToken(null) ;                            
                            navigate('signin');    
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
                                 setToken(null);
                                 setCurrentUser(null);
                                 setIsAdmin(false);                                
                                navigate('/signin'); 
                                                                       
                    }                
                }
            }
            else {
                    // We have a network error
                    console.error('Network error:', err);
                    //navigate('error_server');
            }

                return Promise.reject(err);

            }                               
    );

    return () => {
        api.interceptors.response.eject(requestInterceptor);
        api.interceptors.response.eject(responseIntercetpor);
    };
}, []);


return children;
}

export default ApiErrorHandler;