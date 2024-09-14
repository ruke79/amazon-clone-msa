
import React, { createContext, useContext, useState } from "react";
import { useEffect } from "react";
import api, { getRequest } from "util/api";
import toast from "react-hot-toast";
import TokenUtil from "util/tokenUtil";
import { useErrorBoundary } from "react-error-boundary";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

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

  const [user, setUser] = useState(null);

  //const {showBoundary}= useErrorBoundary();

  const fetchUser = async () => {

    const user = TokenUtil.getUser();

    if (user.email) {

      try {
        const { data } = await getRequest(`/auth/user`);

        const roles = data.roles;

        if (roles.includes("ROLE_ADMIN")) {
          TokenUtil.setAdmin(true);
        } else {
          TokenUtil.removeAdmin();
        }

        setUser(data);


      } catch (error) {
        console.error("Error fetching current user", error);
        toast.error("Error fetching current user");
      }
    }
  }


    useEffect(() => {
      if (token) {
        fetchUser();
      }
    }, [token]);


    return (
      <AuthContext.Provider
        value={{
          token,
          setToken,
          user,
          setUser
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
