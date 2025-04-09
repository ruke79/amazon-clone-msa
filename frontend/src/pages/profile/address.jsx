import ShippingPage from "components/checkout/ShippingPage";
import Layout from "components/profile/Layout";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import DotLoaderSpinner from "components/loader/Loading";
import api, { getRequest } from "util/api";
import { useFetchAddresses } from "hook/hooks";

const Address = () => {

    const { tab } = useLoaderData();
    const { user } = useAuthContext();

    const [addresses, setAddresses] = useState([]);
    const { addressesData, isPendingAddress, isSuccessAddress, updateFetchAddresses } = useFetchAddresses(user.userId, addresses);
    const [selectedAddress, setSelectedAddress] = useState(null);

    if ( isPendingAddress)  {             
        return <DotLoaderSpinner loading={ isPendingAddress} />
    }

    if ( addresses.length === 0 && addressesData && isSuccessAddress ) 
    {
        updateFetchAddresses();                       
        setAddresses(addressesData);        
    }
    

    return (
        <>
            <Layout user={user} tab={tab} title={`${user.userName}'s Address`}>
                <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Addresses</h2>
                </div>
                {<ShippingPage user={user} addresses={addresses} setAddresses={setAddresses}
                    setSelectedAddress={setSelectedAddress} profile={true} />}
            </Layout>
        </>
    );
};

export default Address;


export const loader = async ({ params, request }) => {

    const searchParams = new URL(request.url).searchParams;
    const tab = Number(searchParams.get('tab')) || 0;

    return {
        tab: tab,
    }

};



