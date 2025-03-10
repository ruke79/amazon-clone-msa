import PaymentCheckout from "components/checkout/Payment";
import Layout from "components/profile/Layout";

import { getRequest, putRequest } from "util/api";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import toast from "react-hot-toast";
import { useAuthContext } from "store/AuthContext";

const Payment = () => {

    const { tab } = useLoaderData();
    const {user} = useAuthContext();

    const [dbPM, setDbPM] = useState(user.defaultPaymentMethod);
    const [paymentMethod, setPaymentMethod] = useState(user.defaultPaymentMethod);
    const [error, setErorr] = useState("");

    const handlePM = async () => {
        try {
            const { data } = await putRequest("/user-service/api/user/profile/changepm", null, {
                params : { paymentMethod: paymentMethod }
            });
            setErorr("");
            setDbPM(data);
            
        } catch (error) {
            setErorr(error.response.data.message);
            
        }
    };

    return (
        <>
            <Layout
                user={user}
                tab={tab}
                title={`${user.username}'s Address`}
            >
                <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">
                        My Payment Methods
                    </h2>
                </div>
                <PaymentCheckout
                    paymentMethod={paymentMethod}
                    setPaymentMethod={setPaymentMethod}
                    profile={true}
                />
                <button
                    disabled={!paymentMethod || paymentMethod == dbPM}
                    className={`mt-4 w-52 rounded-xl  text-white p-4 font-semibold text-2xl  ${
                        !paymentMethod || paymentMethod == dbPM
                            ? "cursor-not-allowed bg-slate-400"
                            : "cursor-pointer  bg-amazon-blue_light hover:bg-amazon-blue_dark hover:scale-95 transition"
                    }`}
                    onClick={() => handlePM()}
                >
                    Save
                </button>
                {error && <span className="text-red-500">{error}</span>}
            </Layout>
        </>
    );
};

export default Payment;


export const loader = async ({params, request}) => {
    
        
        const searchParams = new URL(request.url).searchParams;
        const tab = Number(searchParams.get('tab')) || 0;

        return { tab: tab };
        
        // try {

        //     const { data } = await getRequest("/pay-service/api/pay/payment"); 
                        
        
        //     return {
        //           user : data,
        //           tab : tab 
        //     }
        
        // } catch (error) {
        //     toast.error(error);
        // }
};



