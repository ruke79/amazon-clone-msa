import dayjs from 'dayjs'

import {
    SEND_NEW_CHAT_MESSAGE,
    SEND_NEW_MEDIA_MESSAGE,
    SET_CHAT_USERS,
    SET_CONTACT_USERS,
    SET_ROOMS_USER,
    SET_SELECTED_USER_ROOM,
    SET_CONVERSATION_DATA,
    SET_ROOM_CONVERSATION_DATA,
    RECEIVE_NEW_ROOM_MEDIA_MESSAGE,
    RECEIVE_NEW_MEDIA_MESSAGE,
    SEND_NEW_MEDIA_MESSAGE_ROOM,
    SEND_NEW_CHAT_MESSAGE_ROOM,
    RECEIVE_NEW_CHAT_MESSAGE_ROOM,
    RECEIVE_NEW_CHAT_MESSAGE,
    SET_CURRENT_USER,
    SET_SEARCH_DATA,
    SET_SELECTED_USER,
    UPDATE_AUTH_USER,
    ADD_ROOM_TO_LIST,
    UPDATE_LOAD_USER,
    LEAVE_ROOM,
    BAN_USER_FROM_ROOM,
    DELETE_CONTACT,
    FETCH_ERROR,
    FETCH_START,
    FETCH_SUCCESS,
    UPDATE_THEME,

} from "../constants/ActionTypes";

const initialState = {
    users: [],
    contacts: [],
    conversation: [],
    roomConversation: [],
    rooms: [],
    currentUser: null,
    selectedTheme: "dark",
    selectedUser: null,
    selectedRoom: null,
    authUser: "",
    loadUser: false,
    search: "",
    error: "",
    message: "",
    loading: false,
  };

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
      case SEND_NEW_CHAT_MESSAGE_ROOM: {
        return {
          ...state,
          roomConversation: state.roomConversation.concat({
            id: new Date().getTime(),
            user: state.currentUser,
            content: action.payload,
            type: "sent",
            media: "",
            creation_date: moment.utc(),
          }),
        };
      }
      case RECEIVE_NEW_CHAT_MESSAGE_ROOM: {
        return {
          ...state,
          roomConversation: state.roomConversation.concat({
            id: new Date().getTime(),
            sender: action.payload.user,
            content: action.payload.content,
            type: "received",
            media: "",
            creation_date: moment.utc(),
          }),
        };
      }
      case SEND_NEW_MEDIA_MESSAGE_ROOM: {
        return {
          ...state,
          roomConversation: state.roomConversation.concat({
            id: new Date().getTime(),
            user: state.currentUser,
            type: "sent",
            content: "",
            media: action.payload,
            creation_date: moment.utc(),
          }),
        };
      }
      case RECEIVE_NEW_ROOM_MEDIA_MESSAGE: {
        return {
          ...state,
          roomConversation: state.roomConversation.concat({
            id: new Date().getTime(),
            sender: action.payload.user,
            type: "received",
            content: "",
            media: action.payload,
            creation_date: moment.utc(),
          }),
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

export default chatReducer;

