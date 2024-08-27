import { useState } from "react";
import AddShipping from "./AddShipping";
import ListShipping from "./ListShipping";

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
    user,
    addresses,
    setAddresses,
    setSelectedAddress,
    profile,
}) => {


    const [shipping, setShipping] = useState(initialValue);
    const [visible, setVisible] = useState(user?.addresses?.length ? false : true);

        
    return (
        <>
            {!profile && (
                <div className="text-xl font-semibold pb-2 mb-4 border-b border-b-2 ">
                    Shipping Information
                </div>
            )}
            <ListShipping
                visible={visible}
                setVisible={setVisible}
                addresses={addresses}
                setAddresses={setAddresses}
                user={user}
                profile={false}
            />
            {(
                <AddShipping
                    shipping={shipping}
                    setShipping={setShipping}
                    addresses={addresses}
                    setAddresses={setAddresses}
                    initialValue={initialValue}
                    setSelectedAddress={setSelectedAddress}
                />
            )}
        </>
    );
};

export default ShippingPage;
