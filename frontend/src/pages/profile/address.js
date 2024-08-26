import ShippingPage from "components/checkout/ShippingPage";
import Layout from "components/profile/Layout";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import api from "util/api";

const Address = () => {
    
    const { user, tab } = useLoaderData();
    
    //console.log(user);
    
    const [addresses, setAddresses] = useState([user?.address])
    
    console.log(addresses);
    

    return (
        <>
            <Layout user={user} tab={tab} title={`${user.userName}'s Address`}>
            <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Addresses</h2>
            </div>
                { <ShippingPage user={user} addresses={addresses} setAddresses={setAddresses} profile={true}/> }                
            </Layout>            
        </>
    );
};

export default Address;


export const loader = (authContext) => {

    return async ({params, request}) => {
    
        const { currentUser } = authContext;
        const searchParams = new URL(request.url).searchParams;
        const tab = Number(searchParams.get('tab')) || 0;
        console.log(currentUser);

        try {

            const { data } = await api.get("/user/profile/address", 
                            { params : { userId : currentUser.username,             
                                        }
                            } 
            );                                   

            console.log(data);
        
            return {
                  user : data,
                  tab : tab 
            }
        
        } catch (error) {
            console.log("erorr >>>", error.response.data.message);
        }
    };
}



// export async function getServerSideProps(context) {
//     db.connectDb();
//     const { query } = context;
//     const session = await getSession(context);
//     const user = session?.user;
//     const tab = query.tab || 0;

//     if (!session) {
//         return {
//             redirect: {
//                 destination: "/",
//             },
//         };
//     }
//     const addressData = await User.findById(user?.id).select("address").lean();
//     // console.log('add > ', addressData)
//     return {
//         props: {
//             user:{
//                 user: user,
//                 address: JSON.parse(JSON.stringify(addressData)),
//             },
//             tab,
//         },
//     };
// }
