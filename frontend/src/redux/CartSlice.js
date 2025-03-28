import { createSlice } from "@reduxjs/toolkit";

export const CartStatus = Object.freeze({
    
    EMPTY: 'empty',
    UPDATED : 'updated',
    COMMITED : 'commited',
    CANCELED: 'canceled',    
  });
  

const initialState = {
    cartItems: [],
    cartId : null,
    status : CartStatus.EMPTY,
};

export const cartSlice = createSlice({
    name: "cart",
    initialState,
    reducers: {
        addToCart(state, action) {
            state.cartItems.push(action.payload);
        },
        updateCart(state , action) {
            state.cartItems = action.payload;
            //state.status = CartStatus.UPDATED;
        },
        emptyCart(state , action) {
            state.cartItems = [];
            state.cartId = null;
            state.status = CartStatus.EMPTY;
        },
        commitCart(state, action) {
            state.status = CartStatus.COMMITED;
            state.cartId = action.payload;
        },
        cancelCart(state , action) {         
            state.cartItems = [];
            state.status = CartStatus.CANCELED;
        }
    },
});

export const { addToCart, updateCart, emptyCart, commitCart, cancelCart } = cartSlice.actions;

export default cartSlice.reducer;
