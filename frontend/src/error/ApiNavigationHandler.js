import { useRef, useEffect, Children } from 'react';
import { replace, useNavigate } from 'react-router-dom';
import api, { postRequest } from 'util/api';
import TokenUtil from '../util/tokenUtil';
import { useAuthContext } from 'store/AuthContext';
import toast, { Toaster } from 'react-hot-toast';

export function useApiNavigation() {
    // Use useRef to prevent a re-render in the useEffect.
    // A ref, cannot be used as a useEffect dependency, hence,
    // your linters shouldn't complain about missing dependencies.
    const navRef = useRef(useNavigate());    
    const { setToken } = useAuthContext();

    useEffect(() => {


        const resIntercetpor = api.interceptors.response.use(
            (res) => {
                return res;
            },
            async (error) => {

                const msg = error?.response?.data;
                console.log(msg);

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
                        if (msg === "access token expired") {

                        }
                        else {
                            toast.error("Please signin again", { id: 'unauthorized' });
                            setToken(null);    
                            navRef.current('signin');
                        }
                        break;                       
                    default:
                        console.error('Network error:', error);
                        navRef.current('error_server', {replace : true}); 
                        break;
                }
                return Promise.reject(error);
            }
        );

        return () => {
            api.interceptors.response.eject(resIntercetpor);
        };
    }, []);
}

export default function ApiNavigationHandler() {
    useApiNavigation();
    return <></>;
}