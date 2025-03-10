import { useEffect, useState } from "react";

import { useSelector } from "react-redux";
import Header from "pages/header/Header";
import ShippingPage from "components/checkout/ShippingPage";
import Product from "components/checkout/ChekoutProduct";
import Payment from "components/checkout/Payment";
import Summary from "components/checkout/Summary";
import DotLoaderSpinner from "components/loader/Loading";
import { useAuthContext } from "store/AuthContext";
import { useFetcher, useLoaderData } from "react-router-dom";
import api, { getRequest } from "util/api";
import tokenUtil from "util/tokenUtil";
import { useFetchAddresses, useFetchCheckOut, useFetchCart } from "hook/hooks";
import { isPending } from "@reduxjs/toolkit";

const Checkout = () => {

    const { user } = useAuthContext();

    const cart = useSelector((state) => { return state.cart; });

    const { checkoutData, isPendingCheckOut, isSuccessCheckOut} =
     useFetchCheckOut(user.email, cart.cartId, cart.status);

    const { addressesData, isPendingAddress, isSuccessAddress } = useFetchAddresses(user.userId);
        
    const [addresses, setAddresses] = useState([]);        
    const [paymentMethod, setPaymentMethod] = useState(user.defaultPaymentMethod);
    const [totalAfterDiscount, setTotalAfterDiscount] = useState("");
    const [selectedAddress, setSelectedAddress] = useState("");
    const [loading, setLoading] = useState(false);

    
    

     useEffect(() => {

        let check = addresses.find((address) => address.active === true);        
        if (check) {
            setSelectedAddress(check);
         
        } else {
            setSelectedAddress("");
        }

    }, [addresses]);
    
   
    if (isPendingCheckOut || isPendingAddress)  {             
        return <DotLoaderSpinner loading={isPendingCheckOut || isPendingAddress} />
    }

    if ( addresses.length === 0 && isSuccessCheckOut && isSuccessAddress) 
    {       
        
        setAddresses(addressesData);        
    }

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
                    {<Product cart={checkoutData} />}
                </section>

                <section className="col-span-1">
                    <Payment
                        paymentMethod={paymentMethod}
                        setPaymentMethod={setPaymentMethod}
                    />
                    <Summary
                        selectedAddress={selectedAddress}
                        user={user}
                        cart={checkoutData}
                        paymentMethod={paymentMethod}
                        totalAfterDiscount={totalAfterDiscount}
                        setTotalAfterDiscount={setTotalAfterDiscount}
                        setLoading={setLoading}
                    />
                </section>
            </main>            
        </>
    );
};

export default Checkout;


