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

const Cart = () => {
    
    const { user } = useAuthContext();
    
    let cart = useSelector((state) => { return state.cart; });
    const dispatch = useDispatch();

    console.log(cart);


    //const data = useLoaderData();
    const { cartData, isSuccessCart, isPendingCart } = useFetchCart(user.email);
    
    useEffect(() => {

        if ( isSuccessCart) {
            if (cart.cartItems.length === 0 && cartData.length > 0) {
                dispatch(updateCart(cartData));            
            }
        }
    }, [cartData])


    if(isPendingCart) return <div>Loading...</div>;

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
           
            
        // try {

        //     const { data } = await getRequest("/cart-service/api/cart/loadcart", 
        //          { params : { userId : tokenUtil.getUser().email } }
        //     );

        //     return data;
        // }
        // catch (err) {
        //     console.log(err);
        // }
    
}

