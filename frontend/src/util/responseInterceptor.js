import api from "./api";
import TokenUtil from "./tokenUtil";
import { useAuthContext } from "store/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

const ResponseInterceptor = () => {


    const { setToken, setRefeshTokenExpired } = useAuthContext();
    

    useEffect(() => {
        let isRefreshing = false;
        let isRefreshExpired = false;
                
        const intercetpor = api.interceptors.response.use(
            (res) => {
                return res;
            },
            async (err) => {
                let originalConfig = err.config;
                const msg = err.response.data;
                const status = err.response.status;

                console.log(msg);
               
                console.log(originalConfig);

                if (status === 401 && err.response) {

                    if (!isRefreshing) {

                        isRefreshing = true;

                        if (msg === "access token expired") {

                            
                            TokenUtil.removeToken();

                            const rs = await api.post("/cookie/refresh");
                           

                            if (rs.status === 200) {

                                console.log(rs);
                                
                                const accessToken = rs.headers['access'];
                                TokenUtil.updateToken(accessToken);
                                setRefeshTokenExpired(false);
                                setToken(accessToken);

                                //originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;
                                
                            }                                              
                            
                        }        
                        isRefreshing = false;                      
                                                
                        return api(originalConfig);
                    }
                }
                else if (status === 400 && err.response) {

                    console.log("refresh token expired");

                    if (!isRefreshExpired) {

                        isRefreshExpired = true;
                    
                        if (msg === "refresh token expired") {
                            

                            TokenUtil.remove();
                            setRefeshTokenExpired(true);     
                            setToken(null);
                            
                            
                        }                           
                        isRefreshExpired = false;
                        
                        return api(originalConfig);                     
                    }
                    
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