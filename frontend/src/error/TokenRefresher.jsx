import { useRef, useEffect, Children } from 'react';
import { replace, useNavigate } from 'react-router-dom';
import api, { getRequest } from 'util/api';
import TokenUtil from '../util/tokenUtil';
import { useAuthContext } from 'store/AuthContext';
import toast, { Toaster } from 'react-hot-toast';


export function useTokenRefresh() {
    // Use useRef to prevent a re-render in the useEffect.
    // A ref, cannot be used as a useEffect dependency, hence,
    // your linters shouldn't complain about missing dependencies.
    const navRef = useRef(useNavigate());
    const { token, setToken } = useAuthContext();

    const refreshAndRetryQueue = useRef([]);
    const isRefreshing = useRef(false);

    useEffect(() => {

        const reqInterceptor = api.interceptors.request.use(
            async (config) => {

                console.log(token);
              
              if (token) {
                config.headers.Authorization = `Bearer ${token}`;
              }
          
          
              return config;
            },
            (error) => {
              return Promise.reject(error);
            }
          );

        const resInterceptor = api.interceptors.response.use(
            (res) => {
                return res;
            },
            async (error) => {

                let originalConfig = error.config;

                const msg = error?.response?.data;

                switch (error?.response?.status) {

                    case 500:
                        navRef.current('/');
                        break;
                    case 400:
                        if (msg === "refresh token expired") {
                            navRef.current('signin');
                            setToken(null);
                            toast.error("invalid login credentials Please signin again", { id: 'loginError' });
                        }
                        break;
                    case 401:
                        

                        if (!isRefreshing.current) {

                            isRefreshing.current = true;

                            try {

                                if (msg === "access token expired") {

                                    const data = await getRequest("/user-service/api/token/refresh");

                                    if (data.status == 200) {
                                        console.log(data);
                                        const accessToken = data.headers['access'];
                                        //TokenUtil.updateToken(accessToken);

                                        setToken(accessToken);

                                        originalConfig.headers['Authorization'] = `Bearer ${accessToken}`;

                                        refreshAndRetryQueue.current.forEach(({ config, resolve, reject }) => {
                                            api
                                                .request(config)
                                                .then((response) => resolve(response))
                                                .catch((err) => reject(err));
                                        });
        
                                        refreshAndRetryQueue.current.length = 0;

                                        return api(originalConfig);
                                    }

                                }
                                else {
                                    toast.error("Please signin again", { id: 'unauthorized' });
                                    setToken(null);
                                    navRef.current('signin');
                                }

                            } catch (refreshError) {
                                refreshAndRetryQueue.current.length = 0;

                                console.log(refreshError);
                                
                                throw refreshError;

                            } finally {
                                isRefreshing.current = false;

                            }
                        }
                        return new Promise((resolve, reject) => {
                            refreshAndRetryQueue.current.push({ config: originalConfig, resolve, reject });
                        });                        
                    default:
                        //console.error('Network error:', error);
                        //navRef.current('error_server', {replace : true}); 
                        break;
                }
                return Promise.reject(error);
            }
        );

        return () => {
            api.interceptors.request.eject(reqInterceptor);
            api.interceptors.response.eject(resInterceptor);
        };
    }, [token]);
}

export default function TokenRefresher() {
    useTokenRefresh();
    return <></>;
}