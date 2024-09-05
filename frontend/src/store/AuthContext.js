
import React, { createContext, useContext, useState } from "react";
import { useEffect } from "react";
import api from "util/api";
import toast from "react-hot-toast";
import TokenUtil from "util/tokenUtil";

const AuthContext = createContext();

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
  const [refreshTokenExpired, setRefeshTokenExpired] = useState(false);

  //store the current loggedin user
  const [currentUser, setCurrentUser] = useState(null);
  //handle sidebar opening and closing in the admin panel
  const [openSidebar, setOpenSidebar] = useState(true);
  //check the loggedin user is admin or not
  const [isAdmin, setIsAdmin] = useState(isADmin);

  const fetchUser = async () => {
    const user = TokenUtil.getUser();

    if (user?.username) {
      try {
        const { data } = await api.get(`/auth/user`);
        const roles = data.roles;

        if (roles.includes("ROLE_ADMIN")) {
          //localStorage.setItem("IS_ADMIN", JSON.stringify(true));
          TokenUtil.setIsAdmin(true);
          setIsAdmin(true);
        } else {
          //localStorage.removeItem("IS_ADMIN");
          TokenUtil.removeAdmin();
          setIsAdmin(false);
        }
        
        setCurrentUser(data);
      } catch (error) {
        console.error("Error fetching current user", error);
        toast.error("Error fetching current user");
      }
    }
  };

  //if  token exist fetch the current user
  useEffect(() => {
    if (token) {
      fetchUser();      
      console.log("fetchUser");
    }
  }, [token]);

  //through context provider you are sending all the datas so that we access at anywhere in your application
  return (    
    <AuthContext.Provider
      value={{
        token,
        setToken,
        refreshTokenExpired,
        setRefeshTokenExpired,
        currentUser,
        setCurrentUser,
        openSidebar,
        setOpenSidebar,
        isAdmin,        
        setIsAdmin,
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
