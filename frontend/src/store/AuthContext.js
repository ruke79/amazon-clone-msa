
import React, { createContext, useContext, useState } from "react";
import { useEffect } from "react";
import api, { getRequest } from "util/api";
import toast from "react-hot-toast";
import TokenUtil from "util/tokenUtil";
import { useErrorBoundary } from "react-error-boundary";
import { useQuery } from "@tanstack/react-query";

const AuthContext = createContext();

const getUser = async () => {  
  const { data } = await getRequest(`/auth/user`);
  return data;
}

const useUser = () => {

  const userQuery = useQuery({
      queryKey: ['user'],
      queryFn : getUser,
      throwOnError : true,      
  });

  return userQuery;
}


export const ContextProvider = ({ children }) => {
  //find the token in the localstorage
  const getToken = TokenUtil.getToken()
    ? JSON.stringify(TokenUtil.getToken())
    : null;
  //find is the user status from the localstorage
  const isADmin = TokenUtil.isAdmin()
    ? JSON.stringify(TokenUtil.isAdmin())
    : false;

  //store the token
  const [token, setToken] = useState(getToken);
  
  const {showBoundary}= useErrorBoundary();

  const { data, error }  = useUser();
  
  if (error)
    showBoundary(error);

  
  useEffect(()=> {
    
    if (data) {
      const user = TokenUtil.getUser();

      if (user.email) {
        const roles = data.roles;

          if (roles.includes("ROLE_ADMIN")) {            
            TokenUtil.setAdmin(true);  
          } else {            
            TokenUtil.removeAdmin();
          }         
          
        }
      }

  }, [data]);

    
  return (    
    <AuthContext.Provider
      value={{
        token,
        setToken,                        
      }}
    >
      {children}
    </AuthContext.Provider>
    
  );
};



//by using this (useMyContext) custom hook we can reach our context provier and access the datas across our components
export const useAuthContext = () => {
  const context = useContext(AuthContext);

  return context;
};
