import Header from "pages/header/Header";
import MenuSideBar from "./header/MenuSidebar";
import CartPage from "components/cart/CartPage";
import Empty from "components/cart/Empty";
import { useSelector, useDispatch } from "react-redux";
import { getRequest } from "util/api";
import { useFetcher, useLoaderData } from "react-router-dom";
import { addToCart, updateCart } from "../redux/CartSlice";
import { useEffect, useState } from "react";
import tokenUtil from "util/tokenUtil";
import { useFetchCart } from "hook/hooks";
import { useAuthContext } from "store/AuthContext";
import { useCallback } from "react";
import { jwtDecode } from "jwt-decode";
import { loadCart } from "util/api";
import DotLoaderSpinner from "components/loader/Loading";

const Cart = () => {
    
    const { user } = useAuthContext();
    const cart = useSelector((state) => { return state.cart; });
    const dispatch = useDispatch();   
                
    
    const { cartData, isSuccessCart, status, isPendingCart } = useFetchCart(user.email, cart.status, cart.cartId);

    
    useEffect(() => {

        if ( isSuccessCart) {
            if (cart.cartItems.length === 0 && cartData.length > 0) {                
                
                dispatch(updateCart(cartData));                            
            }
        }
    }, [cartData, isPendingCart])


    if(isPendingCart) return <DotLoaderSpinner loading={isPendingCart}/>;

    if(status === "error") return;
    
    

    return (
        <>
            <Header />
            <main className="w-full h-screen">
                {cart.cartItems.length > 0 ? (
                    <CartPage cart={cart} />
                ) : (
                    <Empty />
                )}
                
            </main>
            <MenuSideBar />
        </>
    );
};

export default Cart;

