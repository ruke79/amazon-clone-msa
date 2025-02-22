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

const Cart = () => {
    let cart = useSelector((state) => { return state.cart; });
    const dispatch = useDispatch();

    const data = useLoaderData();
    
    useEffect(() => {

        if (cart.cartItems.length === 0 && data.length > 0) {
            dispatch(updateCart(data));            
        }
    }, [])




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

export const loader = async () => {
           
            
        try {

            const { data } = await getRequest("/cart-service/api/cart/loadcart", 
                 { params : { userId : tokenUtil.getUser().email } }
            );

            return data;
        }
        catch (err) {
            console.log(err);
        }
    
}

