import { createSlice } from "@reduxjs/toolkit";
import React, { useReducer, useContext, createContext, Dispatch } from "react";
import dayjs from 'dayjs'

import {    
    SET_CHAT_USERS,    
    SET_SELECTED_USER_ROOM,    
    SET_SEARCH_DATA,
    SET_SELECTED_USER,    
    ADD_ROOM_TO_LIST,   
    ADD_CHAT_MESSAGE

} from "../constants/ChatActionType";
import { selectedRoom } from "./ChatSelectors";

const initialState  = {
    users: [],
    contacts: [],    
    roomConversation: [],
    rooms: [],    
    selectedUser: null,
    selectedRoom: null,        
    loadUser: false,
    search: "",
    error: "",
    message: "",    
    loading: false,
    image : {file: null, url: "",}
  };

  export const chatSlice = createSlice({
    name : "chat",
    initialState : initialState,
    reducers: {
      addRoomToList : (state, action) => {
        state.rooms.push(action.payload)             
      },
      selectRoom : (state, action) => {
        state.selectedRoom = action.payload;
      },
      setRooms : (state, action) => {
        state.rooms = action.payload;
      },
      setRoomConversation : (state, action) => {
        state.roomConversation = action.payload;
        console.log(action.payload );
        
      },
      fetchRoomConversation : (state, action) => {
        state.roomConversation = [...action.payload, ...state.roomConversation];
      },
      setImage : (state, action) => {
        state.image = action.payload;
      }
      
    }
  });

  export const { addRoomToList, setRooms, setRoomConversation, fetchRoomConversation, selectRoom, setImage } = chatSlice.actions;

  export default chatSlice.reducer;

  const chatReducer = (state = initialState, action) => {
        
    switch (action.type) {
      case SET_CHAT_USERS: {
        return { ...state, users: action.payload };
      }
      case SET_SELECTED_USER: {
        return { ...state, selectedUser: action.payload };
      }
      case SET_SELECTED_USER_ROOM: {
        return { ...state, selectedRoom: action.payload };
      }
      case ADD_ROOM_TO_LIST: {        
        return {
          ...state,
          rooms: state.rooms.concat(action.payload),
        };
      }
      case ADD_CHAT_MESSAGE: {
        return {
          ...state,
          roomConversation: state.roomConversation.concat(action.payload),
        };
      }            
      case SET_SEARCH_DATA: {
        return {
          ...state,
          search: action.payload,
        };
      }
      default:
        return state;
    }
}
