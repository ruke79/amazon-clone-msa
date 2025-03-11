import Payment from "components/order/Payment";
import Header from "pages/header/Header";
import OrderInfo from "components/order/OrderInfo";
import Product from "components/order/Product";
import Total from "components/order/Total";
import UserInfo from "components/order/UserInfo";
import React, { useEffect, useState, useRef } from "react";
import DotLoaderSpinner from "components/loader/Loading"
import api, { getRequest } from "util/api";
import { useLoaderData } from "react-router-dom";
import tokenUtil from "util/tokenUtil";
import { useAuthContext } from "store/AuthContext";
import { useFetchOrder } from "hook/hooks";

const OrderPage = () => {
    const orderId = useLoaderData();
        
    
    const { user } = useAuthContext();

    const { orderData, isPendingOrder, isSuccessOrder, isError }  = useFetchOrder(orderId, user.email);
  
    const [order ,setOrder] = useState(null);
    const [loading, setLoading] = useState(false);
       
    

    if (isPendingOrder ) {        
        return <DotLoaderSpinner loading={isPendingOrder} />
    }
           
    
    return (
        <React.Fragment>
      
            <Header title="Full Amazon Clone React" />
            <main className="max-w-screen-2xl mx-auto bg-gray-100 grid grid-cols-3 md:px-10 pt-5 pb-8 gap-8">
                <section className="col-span-2 bg-white p-2 md:p-5 rounded-xl border">
                    <OrderInfo order={orderData} />

                    {orderData.products.map((product,i) => (
                        <Product key={i} product={product} />
                    ))}

                    <Total order={orderData} />
                </section>
                <section className="md:col-span-1 h-fit bg-white p-2 md:p-5 rounded-xl border">
                    <UserInfo order={orderData} user={user} />
                    { (orderData.orderStatus === "NOT_PROCESSED" || orderData.orderStatus === "UNPAID" ) 
                    && (<Payment order={orderData} setOrder={setOrder} setLoading={setLoading} profile={false} />)}
                    
                </section>
            </main>
        </React.Fragment>
    );
};

export default OrderPage;


export const loader = async ({params, request}) => {
            
        const orderId = params.id;

        return orderId;     
    
}

