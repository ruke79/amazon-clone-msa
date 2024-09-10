import Header from "pages/header/Header";
//import MenuSideBar from "components/Header/MenuSidebar";
import CartPage from "components/cart/CartPage";
import Empty from "components/cart/Empty";
import { useSelector, useDispatch } from "react-redux";
import { getRequest   } from "util/api";
import { useFetcher, useLoaderData } from "react-router-dom";
import { updateCart } from "../redux/CartSlice";
import { useEffect, useState } from "react";

const Cart = () => {
    let cart  = useSelector((state) => { return state.cart; });
    const dispatch = useDispatch();

    const data = useLoaderData();
    
    useEffect(() => {

        if (data.length > 0) {        
            dispatch(updateCart(data));
        } 
    },[])
          
       
        
                 
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
            {/* <MenuSideBar /> */}
        </>
    );
};

export default Cart;

export const loader = (authContext) => {

    return async ({ params, request }) => {

        
        try {

            const { data } = await getRequest("/user/cart/loadcart");

            return data;
        }
        catch(err) {
            console.log(err);
        }
    };
}

