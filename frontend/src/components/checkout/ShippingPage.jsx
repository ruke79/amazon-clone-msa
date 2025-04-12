import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import AddShipping from "./AddShipping";
import ListShipping from "./ListShipping";

import { useFetchAddresses } from "hook/hooks";

const initialValue = {
    firstname: "",
    lastname: "",
    phoneNumber: "",
    state: "",
    city: "",
    zipCode: "",
    address1: "",
    address2: "",
    country: "",
};

const ShippingPage = ({    
    setSelectedAddress,
    user,    
    setLoading,
    profile
}) => {
    
    

    const [shipping, setShipping] = useState(initialValue);
    const { addressesData, isPendingAddress, invalidate, isSuccessAddress } = useFetchAddresses(user.userId);
         
        
     useEffect(() => {
    
            
        if (isSuccessAddress && addressesData ) {  // empty null value
           
            const check = addressesData.find((address) => address.active === true);
            if (check) {
                setSelectedAddress(check);

            } else {
                setSelectedAddress("");
            }
            
        }
    

    }, [addressesData]);


if (isPendingAddress) {
    //return <DotLoaderSpinner loading={isPendingAddress} />
    return;
}
    

            
    return (
        <>
            {(!profile &&
                <div className="text-xl font-semibold pb-2 mb-4 border-b border-b-2 ">
                    Shipping Information
                </div>
            )}
            <ListShipping
                setLoading={setLoading}      
                addresses = {addressesData}                          
                invalidate= {invalidate}    
                user={user}
                profile={profile}
            />
            {(
                <AddShipping                    
                    setLoading={setLoading}                         
                    addresses = {addressesData}      
                    invalidate= {invalidate}    
                    shipping={shipping}
                    setShipping={setShipping}                    
                    initialValue={initialValue}                    
                />
            )}
        </>
    );
};

export default ShippingPage;
