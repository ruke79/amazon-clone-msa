import Layout from "components/profile/Layout";

import { ordersLinks } from "components/profile/sidebar/ordersLinks";
import { Link, useLoaderData, useSearchParams} from "react-router-dom";
import { CheckBadgeIcon, EyeIcon, XCircleIcon } from "@heroicons/react/24/solid";
import slugify from "slugify";
import api, { getRequest } from "util/api";
import toast from "react-hot-toast";
import { useAuthContext } from "store/AuthContext";


const Orders = () => {

     const searchParams = useSearchParams();     
     const qParam = searchParams[0].get('q');
     console.log(qParam?.split("__")[1]);

     const { user} = useAuthContext();
     const { orders, tab } = useLoaderData();

     console.log(user);
     
        
    return (
        <>
            <Layout user={user} tab={tab} title={`${user.name}'s Orders`}>
                <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Orders</h2>
                    <nav>
                        <ul className="flex">
                            {ordersLinks.map((order, i) => (
                                <li className={`${qParam?.split("__")[0] == slugify(order.name, {lower:true})  ? 'font-bold border-b' : '' } px-1 flex items-center justify-center hover:font-bold hover:border-b`} key={i}>
                                    <Link to={`profile/orders?tab=${tab}&q=${slugify(order.name,{lower: true,})}__${order.filter}`}>{order.name}</Link>
                                </li>
                            ))}
                        </ul>
                    </nav>

                    <table className="table_order">
                        <thead>
                            <tr>
                                <td>Order id</td>
                                <td>Products</td>
                                <td>Payment Method</td>
                                <td>Total</td>
                                <td>Paid</td>
                                <td>Status</td>
                                <td>View</td>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((order, i) => (
                                <tr key={i}>
                                    <td>{order.id}</td>
                                    <td className="flex">
                                        {order.products.map((p) => (
                                            <img
                                                key={p.id}
                                                src={p.image}
                                                alt={p.name}
                                                width={50}
                                                height={50}
                                                className="rounded-full mx-1"
                                            />
                                        ))}
                                    </td>
                                    <td>{order.paymentMethod}</td>
                                    <td>{order.total}$</td>
                                    <td>
                                        {order.isPaid == true ? (
                                            <CheckBadgeIcon className="w-8 h-8 fill-green-500" />
                                        ) : (
                                            <XCircleIcon className="w-8 h-8 fill-red-500" />
                                        )}
                                    </td>
                                    <td>{order.status}</td>
                                    <td><Link to={`/order/${order.id}`}><EyeIcon className="w-8 h-8 fill-slate-500 cursor-pointer hover:fill-slate-800" /></Link></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </Layout>
        </>
    );
};

export default Orders;

export const loader = (authContext) => {

    return async ({params, request}) => {
    
        //const { currentUser } = authContext;
        const searchParams = new URL(request.url).searchParams;
        
        const tab = Number(searchParams.get('tab')) || 0;
        

        const filter = searchParams.get('q').split("__")[1];

        console.log(filter);

        try {

         const { data } = await getRequest("/order-service/api/order/orders", 
                      { params : { filter : filter }                                  
                       } 
            );                             
        
          return { orders : data, tab : tab };    
        
        } catch (error) {
            toast.error("Failed to load orders");            
        }

        return { orders : [], tab : tab };    
    };
}

