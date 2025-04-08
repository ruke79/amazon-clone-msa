import { useRef, useEffect, useState, useCallback, Children } from 'react';
import { replace, useNavigate } from 'react-router-dom';
import api, { getRequest } from 'util/api';
import TokenUtil from './tokenUtil';
import { useAuthContext } from 'store/AuthContext';
import toast, { Toaster } from 'react-hot-toast';
import { jwtDecode } from 'jwt-decode';


export function useTokenRefresh() {
    // Use useRef to prevent a re-render in the useEffect.
    // A ref, cannot be used as a useEffect dependency, hence,
    // your linters shouldn't complain about missing dependencies.
    const navRef = useRef(useNavigate());
        
    const refreshAndRetryQueue = useRef([]);
    const isRefreshing = useRef(false);

    const { login, logout, token, isAuthenticated, expiresAt } = useAuthContext();
    const tokenRef = useRef(token);
  
  const refreshAccessToken = useCallback(
    async () => {
    try {
      const response = await getRequest("/user-service/api/token/refresh");
      
      console.log(response.status);
      if (response.status === 204) {
        logout();
      } else if (response.status === 200) {
          const accessToken = response.headers['access'];           
          const decodedToken = jwtDecode(accessToken);
          const user = {
            email: decodedToken.sub,
            roles: decodedToken.roles ? decodedToken.roles.split(",") : [],
        };          
          const expiredAt = decodedToken.exp;
        login(user, accessToken, expiredAt);
        
      }
    } catch (error) {
      logout();
    }
  }, [login, logout]);

  useEffect(() => {
    
    if (isAuthenticated) {
      refreshAccessToken();
    }
  }, [refreshAccessToken]);


   useEffect(() => {
    
    let refreshAccessTokenTimerId;

    if (isAuthenticated) {
      refreshAccessTokenTimerId = setTimeout(() => {
        refreshAccessToken();
      }, new Date(expiresAt).getTime() * 1000 - Date.now() - 10 * 1000);
    }

    return () => {
      if (isAuthenticated && refreshAccessTokenTimerId) {
        clearTimeout(refreshAccessTokenTimerId);
      }
    };
  }, [expiresAt, isAuthenticated, refreshAccessToken]);


    const [prevToken, setPrevToken] = useState(null);
    if( prevToken !== token) {
        setPrevToken(token);

        
        tokenRef.current = token;
    }

    useEffect(() => {

        const reqInterceptor = api.interceptors.request.use(
            async (config) => {
                            
               
              if (tokenRef.current) {                                   
                config.headers.Authorization = `Bearer ${tokenRef.current}`;
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
                    case 503:
                            navRef.current('error_server');
                            logout();                            
                            break;
                    case 400:
                        if (msg === "refresh token expired") {
                            logout();
                            navRef.current('signin');
                                                        
                            toast.error("invalid login credentials Please signin again", { id: 'loginError' });
                        }
                        break;
                    case 401:
                        

                        if (!isRefreshing.current) {

                            isRefreshing.current = true;

                            try {

                                
                                    const response = await getRequest("/user-service/api/token/refresh");

                                    if (response.status == 200) {
                                        console.log(response);
                                        const accessToken = response.headers['access'];           
                                        const decodedToken = jwtDecode(accessToken);
                                        const user = {
                                          email: decodedToken.sub,
                                          roles: decodedToken.roles ? decodedToken.roles.split(",") : [],
                                      };          
                                        const expiredAt = decodedToken.exp;
                                      login(user, accessToken, expiredAt);
                                      
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