import { useEffect, useState } from "react";

import Header from "pages/header/Header";
//import MenuSideBar from "@/components/Header/MenuSidebar";
import ShippingPage from "components/checkout/ShippingPage";
import Product from "components/checkout/ChekoutProduct";
import Payment from "components/checkout/Payment";
import Summary from "components/checkout/Summary";
import DotLoaderSpinner from "components/loader/Loading";
import { useAuthContext } from "store/AuthContext";
import { useFetcher, useLoaderData } from "react-router-dom";
import api from "util/api";

const Checkout = () => {

        
    const cart = useLoaderData();
    const user = { userId : cart.userId, address : cart.address};
    
        
    const [addresses, setAddresses] = useState(user?.address || []);
    const [paymentMethod, setPaymentMethod] = useState("paypal");
    const [totalAfterDiscount, setTotalAfterDiscount] = useState("");
    const [selectedAddress, setSelectedAddress] = useState("");
    const [loading, setLoading] = useState(false);
                     
       
    
    useEffect(() => {
                     
        let check = addresses?.find((address) => address.active == true);
        if (check) {
            setSelectedAddress(check);
            console.log(selectedAddress);
        } else {
            setSelectedAddress("");
        }
        
    }, [addresses]);

    

    return (
        <>
            {loading && <DotLoaderSpinner loading={loading} />}
            <Header />
            <main className="grid grid-cols-3 md:px-10 mb-10 py-4 gap-8 ">
                <section className="col-span-2">
                    <ShippingPage
                        user={user}
                        addresses={addresses}
                        setAddresses={setAddresses}
                        setSelectedAddress={setSelectedAddress}
                    />
                    { <Product cart={cart} /> }
                </section>

                <section className="col-span-1">
                    <Payment
                        paymentMethod={paymentMethod}
                        setPaymentMethod={setPaymentMethod}
                    />
                    <Summary
                        selectedAddress={selectedAddress}
                        user={user}
                        cart={cart}
                        paymentMethod={paymentMethod}
                        totalAfterDiscount={totalAfterDiscount}
                        setTotalAfterDiscount={setTotalAfterDiscount}
                        setLoading={setLoading}
                    />
                </section>
            </main>
            {/* <MenuSideBar /> */}
        </>
    );
};

export default Checkout;


export const loader = (authContext) => {

    return async ({params, request}) => {
    
        const { currentUser } = authContext;

        const { data } = await api.get("/user/cart/checkout");       
        
        
        return data;    
    };
}
