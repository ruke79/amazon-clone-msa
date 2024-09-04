import api from "./api";
import TokenUtil from "./tokenUtil";
import { useAuthContext } from "store/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import CookiUtil from "./cookieUtil";

const ResponseInterceptor = () => {


    const { setToken, RefeshTokenExpired, setRefeshTokenExpired, setIsAdmin, setCurrentUser } = useAuthContext();


    useEffect(() => {
        let isRefreshing = false;
        

        const refreshAndRetryQueue = [];

        const intercetpor = api.interceptors.response.use(
            (res) => {
                return res;
            },
            async (err) => {
                let originalConfig = err.config;
                const msg = err.response.data;
                const status = err.response.status;

                if (status === 401 && err.response) {

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
                            CookiUtil.delete('REFRESH');
                            //window.location.replace('/signin');
                            setRefeshTokenExpired(true);
                            throw error;  

                        } finally {
                            isRefreshing = false;
                        }
                    }

                    return new Promise((resolve, reject) => {
                        refreshAndRetryQueue.push({ config: originalConfig, resolve, reject })
                    });

                }
                else if (status === 400 && err.response) {


                    if (!isRefreshing) {

                        isRefreshing = true;

                        if (msg === "refresh token expired") {

                            TokenUtil.remove();
                            setToken(null);
                            setCurrentUser(null);
                            setIsAdmin(false);
                            setRefeshTokenExpired(true);

                            CookiUtil.delete('REFRESH');                            

                            originalConfig.headers['Cookie'] = null;

                            //window.location.replace('/signin');
                        }

                        isRefreshing = false;

                        return api(originalConfig);
                    }     
                    
                    
                }
                if (!err.response) {
                    // We have a network error
                    console.error('Network error:', err);
                }                  

                return Promise.reject(err);

            }
        );

        return () => {
            api.interceptors.response.eject(intercetpor);
        };
    }, []);


    return <></>;
}

export default ResponseInterceptor;