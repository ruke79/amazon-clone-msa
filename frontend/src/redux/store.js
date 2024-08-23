import { configureStore,combineReducers, getDefaultMiddleware } from "@reduxjs/toolkit";
import menuReducer from "./MenuSlice";
import dialogReducer from "./DialogSlice";
import cartReducer from "./CartSlice";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // defaults to localStorage for web



const rootReducer = combineReducers({
    menu: menuReducer,
    dialog: dialogReducer,
    cart: cartReducer,
});

const persistConfig = {
    key: "root",
    storage,
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
    reducer: persistedReducer,
    devTools: process.env.NODE_ENV !== "production"  ,  
     middleware : 
     (getDefaultMiddleware) =>
    getDefaultMiddleware({
        serializableCheck: false,
    })
})

// Infer the `RootState` and `AppDispatch` types from the store itself
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
//export type AppDispatch = typeof store.dispatch;


export const persistor = persistStore(store)

