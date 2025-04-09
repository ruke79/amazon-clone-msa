
import React, { createContext, useContext, useState, useCallback, useMemo } from "react";
import { useEffect } from "react";
import api, { getRequest } from "util/api";
import toast from "react-hot-toast";
import TokenUtil from "util/tokenUtil";
import { useErrorBoundary } from "react-error-boundary";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";


export const STATUS = Object.freeze({
  IDLE: 'idle',
  PENDING: 'pending',
  SUCCEEDED: 'succeeded',
  FAILED: 'failed',
});

const initialState = {
  user: {},
  token: null,
  expiresAt: null,
  isAuthenticated: false,
  status: STATUS.PENDING,
};


const AuthContext = createContext({
  ...initialState,
  login: (user = {}, token = '', expiresAt = '') => {},
  logout: () => {},
  updateUser: () => {},
  updateToken: () => {},
  setAuthenticationStatus: () => {},
});

const authReducer = (state, action) => {
  switch (action.type) {
    case 'login': {      
      return {            
        user : action.payload.user, 
        token: action.payload.token,
        expiresAt: action.payload.expiresAt,
        isAuthenticated: true,
        verifyingToken: false,
        status: STATUS.SUCCEEDED,
      };
    }
    case 'logout': {
      return {
        ...initialState,
        status: STATUS.IDLE,
      };
    }
    case 'updateUser': {
      console.log(action.payload.user);
      return {
        ...state,
        user: action.payload.user,
      };
    }
    case 'updateToken': {
      return {
        ...state,
        user: action.payload.token,
      };
    }
    case 'status': {
      return {
        ...state,
        status: action.payload.status,
      };
    }
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
};


export const ContextProvider = ({ children }) => {
  //find the token in the localstorage  
  //find is the user status from the localstorage
  const isADmin = TokenUtil.isAdmin()
    ? JSON.stringify(TokenUtil.isAdmin())
    : false;

 

  const [state, dispatch] = React.useReducer(authReducer, initialState);

  const login = React.useCallback((user,token, expiresAt) => {
    dispatch({
      type: 'login',
      payload: {        
        user,
        token,
        expiresAt,
      },
    })
  }, []);

  const logout = React.useCallback(() => {
    dispatch({
      type: 'logout',
    });
  }, []);
  
  const updateUser = React.useCallback((user) => {
    dispatch({
      type: 'updateUser',
      payload: {
        user,
      },
    });
  }, []);
  const updateToken = React.useCallback((token) => {
    dispatch({
      type: 'updateToken',
      payload: {
        token,
      },
    });
  }, []);

  const setAuthenticationStatus = React.useCallback((status) => {
    dispatch({
      type: 'status',
      payload: {
        status,
      },
    });
  }, []);

  const value = React.useMemo(
    () => ({ ...state, login, logout, updateUser, setAuthenticationStatus, updateToken }),
    [state, setAuthenticationStatus, login, logout, updateUser, updateToken]
  );


    const token = useSelector((state) => { return state.token } );

   const fetchUser = async () => {

     

    try {
      const { data } = await getRequest(`/user-service/api/auth/user`);

      console.log(data);  

      const roles = data.roles;

      if (roles.includes("ROLE_ADMIN")) {
        TokenUtil.setAdmin(true);
      } else {
        TokenUtil.removeAdmin();
      }

      updateUser(data);
      

    } catch (error) {
      console.error("Error fetching current user", error);
      toast.error("Error fetching current user");
    }
  }


  useEffect(() => {
       
       if (token)
        fetchUser();
    
  }, [token]);


  return (
    <AuthContext.Provider
      value={value}
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
