import api from "util/api";
import TokenUtil from "util/tokenUtil";
import { useAuthContext } from "store/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";



function ApiErrorHandler({ children }) {

   

    const { token, setToken, RefeshTokenExpired, setRefeshTokenExpired, setIsAdmin, setCurrentUser } = useAuthContext();
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
                const msg = err.response.data;
                const status = err.response.status;

                if (err.response) {

                if (status === 401 ) {

                    if (!isRefreshing) {

                        isRefreshing = true;

                        try {

                            if (msg === "access token expired" && !RefeshTokenExpired) {


                                TokenUtil.removeToken();

                                const rs = await api.post("/token/refresh");


                                if (rs.status === 200) {

                                    console.log(rs);
                                    const accessToken = rs.headers['access'];
                                    TokenUtil.updateToken(accessToken);
                                    setRefeshTokenExpired(false);
                                    setToken(accessToken);

                                    originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;

                                }
                            }
                            refreshAndRetryQueue.forEach(({ config, resolve, reject }) => {
                                api
                                    .request(config)
                                    .then((response) => resolve(response))
                                    .catch((err) => reject(err));
                            });

                            refreshAndRetryQueue.length = 0;

                            return api(originalConfig);
                        } catch (error) {
                            // Handle token refresh error
                            // You can clear all storage and redirect the user to the login page
                            TokenUtil.remove();                            
                            //window.location.replace('/signin');
                            setRefeshTokenExpired(true);
                            
                           // throwAsyncError(error);
                            throw error;

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
                        setRefeshTokenExpired(true);
                    
                        navigate('/signin');
                        
                    }
                    
                    //throwAsyncError(err);                    

                    //return api(originalConfig);
                }
            }
            else {
                    // We have a network error
                    console.error('Network error:', err);
                    navigate('error_server');
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