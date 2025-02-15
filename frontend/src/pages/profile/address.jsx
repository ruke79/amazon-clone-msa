import ShippingPage from "components/checkout/ShippingPage";
import Layout from "components/profile/Layout";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import api, { getRequest } from "util/api";

const Address = () => {
    
    const { user, tab } = useLoaderData();
    
        
    const [addresses, setAddresses] = useState(user?.addresses);
    const [selectedAddress, setSelectedAddress] = useState(null);
    
    console.log(user);
    

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
    
        //const { currentUser } = authContext;
        const searchParams = new URL(request.url).searchParams;
        const tab = Number(searchParams.get('tab')) || 0;
        
        try {

            const { data } = await getRequest("/user/profile/address"); 
                                                 
            
        
            return {
                  user : data,
                  tab : tab 
            }
        
        } catch (error) {
            console.log("erorr >>>", error.response.data.message);
        }
    };
}


