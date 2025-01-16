import React, { useReducer, useContext, createContext, Dispatch } from "react";

const ChatStateContext = createContext(null);
const ChatDispatchContext = createContext(null);


function reducer(state, action) {
    switch (action.type) {
      case "SetName":
        return {
          ...state,
          name: action.name,
        };
      case "SetRooms":
        return {
          ...state,
          rooms: action.rooms, // text가 자동완성되며, string 타입인걸 알 수 있습니다.
        };   
      default:
        throw new Error("Unhandled action");
    }
  }

  export const ChatProvider = ({ children }) => {

    const [state, dispatch] = useReducer(reducer, {
        name: "",
        rooms: [],
      });

      return (
        <ChatStateContext.Provider value={state}>
          <ChatDispatchContext.Provider value={dispatch}>
            {children}
          </ChatDispatchContext.Provider>
        </ChatStateContext.Provider>
      );

  }

  export function useChatState() {
    const state = useContext(ChatStateContext);
    if (!state) throw new Error("Cannot find ChatStateContext"); 
    return state;
  }
  
  export function useChatDispatch() {
    const dispatch = useContext(ChatDispatchContext);
    if (!dispatch) throw new Error("Cannot find ChatDispatchContext"); 
    return dispatch;
  }