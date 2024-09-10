import { useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from 'util/api';

export function useApiNavigation() {
  // Use useRef to prevent a re-render in the useEffect.
  // A ref, cannot be used as a useEffect dependency, hence,
  // your linters shouldn't complain about missing dependencies.
  const navRef = useRef(useNavigate());

  useEffect(() => {
    const intercetpor = api.interceptors.response.use(
      (response) => response,
      (error) => {
        switch (error?.response?.status) {
          case 500:
            navRef.current('/');
            break;
          default:
            navRef.current('/');
            break;
        }
        return Promise.reject(error);
      }
    );

    return () => {
      api.interceptors.response.eject(intercetpor);
    };
  }, []);
}

export default function ApiNavigation() {
  useApiNavigation();
  return <></>;
}