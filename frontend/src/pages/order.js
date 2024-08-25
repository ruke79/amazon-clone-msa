import Payment from "components/order/Payment";
import Header from "pages/header/Header";
import OrderInfo from "components/order/OrderInfo";
import Product from "components/order/Product";
import Total from "components/order/Total";
import UserInfo from "components/order/UserInfo";
import { useEffect, useState } from "react";
import DotLoaderSpinner from "components/loader/Loading"
import api from "util/api";

const OrderPage = ({ order: orderData }) => {
    const [order ,setOrder] = useState(orderData);
    const [loading, setLoading] = useState(false);
    
    return (
        <>
        {
            loading && (
                <DotLoaderSpinner loading={loading} />
            )
        }
            <Header title="Full Amazon Clone React" />
            <main className="max-w-screen-2xl mx-auto bg-gray-100 grid grid-cols-3 md:px-10 pt-5 pb-8 gap-8">
                <section className="col-span-2 bg-white p-2 md:p-5 rounded-xl border">
                    <OrderInfo order={order} />

                    {order.products.map((product,i) => (
                        <Product key={i} product={product} />
                    ))}

                    <Total order={order} />
                </section>
                <section className="md:col-span-1 h-fit bg-white p-2 md:p-5 rounded-xl border">
                    <UserInfo order={orderData} />
                    {order.isPaid == false && (<Payment order={order} setOrder={setOrder} setLoading={setLoading} profile={false} />)}
                </section>
            </main>
        </>
    );
};

export default OrderPage;

// export async function getServerSideProps(context) {
//     const { query } = context;
//     const id = query.id;
//     const order = await Order.findById(id).populate("user").lean();

//     return {
//         props: {
//             order: JSON.parse(JSON.stringify(order)),
//         },
//     };
// }

export const loader = (authContext) => {

    return async ({params, request}) => {
    
        const { currentUser } = authContext;

        const { data } = await api.get("/user/order", 
        { params : { userId : currentUser.username } } 
        );                       
        
        return data;    
    };
}
