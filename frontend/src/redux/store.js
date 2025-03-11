import { configureStore,combineReducers, getDefaultMiddleware } from "@reduxjs/toolkit";
import menuReducer from "./MenuSlice";
import dialogReducer from "./DialogSlice";
import cartReducer from "./CartSlice";
import chatReducer from "./ChatSlice";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // defaults to localStorage for web


const rootReducer = combineReducers({
    menu: menuReducer,
    dialog: dialogReducer,
    cart: cartReducer,     
    chat: chatReducer,
});

const persistConfig = {
    key: "root",
    storage,
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
    reducer: persistedReducer,
    devTools: process.env.REACT_APP_PROFILE !== "production"  ,  
     middleware : 
     (getDefaultMiddleware) =>
    getDefaultMiddleware({
        serializableCheck: false,
    })
})


export const persistor = persistStore(store)

