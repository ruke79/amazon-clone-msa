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

} from "../constants/ChatActionType";



export const onUserSelect = (user) => {
    return (dispatch) => {
        dispatch({
            type: SET_SELECTED_USER,
            payload: user,
        });
    };
};

export const onRoomSelect = (user) => {
    return (dispatch) => {
      dispatch({
        type: SET_SELECTED_USER_ROOM,
        payload: user,
      });
    };
  };
  
  export const setCurrentUser = (user) => {
    return (dispatch) => {
      dispatch({
        type: SET_CURRENT_USER,
        payload: user,
      });
    };
  };
  export const setSearchData = (search) => {
    return (dispatch) => {
      dispatch({
        type: SET_SEARCH_DATA,
        payload: search,
      });
    };
  };
  
  export const sendNewChatMessage = (message) => {
    return (dispatch) => {
      dispatch({
        type: SEND_NEW_CHAT_MESSAGE,
        payload: message,
      });
    };
  };
  
  export const sendNewChatMessageRoom = (message) => {
    return (dispatch) => {
      dispatch({
        type: SEND_NEW_CHAT_MESSAGE_ROOM,
        payload: message,
      });
    };
  };
  
  export const receiveNewChatMessageRoom = (data) => {
    return (dispatch) => {
      dispatch({
        type: RECEIVE_NEW_CHAT_MESSAGE_ROOM,
        payload: data,
      });
    };
  };
  export const receiveNewChatMessage = (data) => {
    return (dispatch) => {
      dispatch({
        type: RECEIVE_NEW_CHAT_MESSAGE,
        payload: data,
      });
    };
  };
  
  export const sendMediaMessage = (file) => {
    return (dispatch) => {
      dispatch({
        type: SEND_NEW_MEDIA_MESSAGE,
        payload: file,
      });
    };
  };
  
  export const sendRoomMediaMessage = (file) => {
    return (dispatch) => {
      dispatch({
        type: SEND_NEW_MEDIA_MESSAGE_ROOM,
        payload: file,
      });
    };
  };
  
  export const receiveMediaMessage = (file) => {
    return (dispatch) => {
      dispatch({
        type: RECEIVE_NEW_MEDIA_MESSAGE,
        payload: file,
      });
    };
  };
  
  export const receiveRoomMediaMessage = (file) => {
    return (dispatch) => {
      dispatch({
        type: RECEIVE_NEW_ROOM_MEDIA_MESSAGE,
        payload: file,
      });
    };
  };

  export const setChatUsers = (data) => {
    return (dispatch) => {
      dispatch({
        type: SET_CHAT_USERS,
        payload: data,
      });
    };
  };
  
  export const setRoomsUser = (data) => {
    return (dispatch) => {
      dispatch({
        type: SET_ROOMS_USER,
        payload: data,
      });
    };
  };
  
  export const setConversation = (data) => {
    return (dispatch) => {
      dispatch({
        type: SET_CONVERSATION_DATA,
        payload: data,
      });
    };
  };
  
  export const setRoomConversation = (data) => {
    return (dispatch) => {
      dispatch({
        type: SET_ROOM_CONVERSATION_DATA,
        payload: data,
      });
    };
  };
  
  export const leaveRoom = (room_name) => {
    return (dispatch) => {
      dispatch({
        type: LEAVE_ROOM,
        payload: room_name,
      });
    };
  };
  
  export const addRoomToList = (room) => {    
    return (dispatch) => {
      dispatch({
        type: ADD_ROOM_TO_LIST,
        payload: room,
      }).then(response => console.log(response))
      .catch(err => console.log(err));
    };
  };
