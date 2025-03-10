import ShippingPage from "components/checkout/ShippingPage";
import Layout from "components/profile/Layout";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import api, { getRequest } from "util/api";

const Address = () => {
    
    const { userAddresses, tab } = useLoaderData();
    
    const { user } = useAuthContext();
    const [addresses, setAddresses] = useState(userAddresses);
    const [selectedAddress, setSelectedAddress] = useState(null);
    
        

    return (
        <>
            <Layout user={user} tab={tab} title={`${user.userName}'s Address`}>
            <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Addresses</h2>
            </div>
                { <ShippingPage user={user} addresses={addresses} setAddresses={setAddresses}
                setSelectedAddress = {setSelectedAddress} profile={true}/> }                
            </Layout>            
        </>
    );
};

export default Address;


export const loader = (authContext) => {

    return async ({params, request}) => {    
        
        const searchParams = new URL(request.url).searchParams;
        const tab = Number(searchParams.get('tab')) || 0;
        
        try {

            const { data } = await getRequest("/user-service/api/user/profile/address"); 
            
            return {
                  userAddresses : data,
                  tab : tab 
            }
        
        } catch (error) {
            console.log("erorr >>>", error.response.data.message);
        }
    };
}


