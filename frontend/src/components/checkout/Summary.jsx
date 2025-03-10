import { Form, Formik } from "formik";
import { useEffect, useState, useRef } from "react";
import * as Yup from "yup";
import ShippingInput from "./ShippingInput";
import { useNavigate } from "react-router-dom";
import api, { applyCoupon, postRequest } from "util/api";
import tokenUtil from "util/tokenUtil";
import { useFetchCoupons } from "hook/hooks";
import { useAuthContext } from "store/AuthContext";



const Summary = ({
    selectedAddress,
    user,
    cart,
    paymentMethod,
    totalAfterDiscount,
    setTotalAfterDiscount,
    setLoading
}) => {
    const [coupon, setCoupon] = useState("");
    const [discount, setDiscount] = useState("");
    const [error, setError] = useState("");
    const [order_error, setOrder_Error] = useState("");
    const count = useRef(0);
    
    const navigate = useNavigate();

    const validate = Yup.object({
        coupon: Yup.string()
            .required("Please enter a coupon first!")
            .min(3, "Coupon code must Between 3 and 20 character")
            .max(20, "Coupon code must Between 3 and 20 character"),
    });

    
    const { coupons, isSuccess, isPending } = useFetchCoupons(user.email);

      
   if (isPending) return <div><p>Loading...</p></div>  

   
    const applyCouponHandler = async (applycoupon="") => {
        //setLoading(true);
        
          const result = await applyCoupon(cart.cartTotal, applycoupon ? applycoupon : coupon, user.email);

          
        if (result.message) {
            setError(result.message);
            setDiscount("");
            setTotalAfterDiscount("");
            //setLoading(false);
        } else {
            setTotalAfterDiscount(result.totalAfterDiscount);
            setDiscount(result.discount);
            setError("");
            //setLoading(false);
        }
    };

    const placeOrderHandler = async () => {
        try {
            //setLoading(true);
            if (paymentMethod == "") {
                setOrder_Error("please choose a payment method.");
                //setLoading(false);
                return;
            } else if (!selectedAddress) {
                setOrder_Error("please choose a shipping address.");
                //setLoading(false);
                return;
            }

                                    
            const {data}  = await postRequest("/order-service/api/order/create", {                             
                products: cart.products,
                shippingAddress: selectedAddress,
                paymentMethod,
                total:
                    totalAfterDiscount !== ""
                        ? totalAfterDiscount
                        : cart.cartTotal,
                totalBeforeDiscount: cart.cartTotal,
                couponApplied: coupon,
                email : user.email
            });

                        
            navigate(`/order/${data.orderId}`);
            //setLoading(false);
        } catch (error) {
            //setLoading(false);
            setOrder_Error(error.response?.data.message);
        }
    };

    if(coupon ==="" && isSuccess) {

        if (coupons?.length > 0) {
            setCoupon(coupons[0]);            
            applyCouponHandler(coupons[0]);      
        }
    }


   
    return (
        <div className="mt-3">
            <h3 className=" pb-2 mb-4 border-b border-b-2  text-xl font-semibold">
                Order Summary
            </h3>
            <div>
                <Formik
                    enableReinitialize
                    initialValues={{ coupon }}
                    validationSchema={validate}
                    onSubmit={() => {
                        applyCouponHandler();
                    }}
                >
                    {(formik) => (
                        <Form>
                            <ShippingInput
                                name="coupon"
                                // placeholder="*Coupon"
                                onChange={(e) => setCoupon(e.target.value)}
                            />
                            {error && (
                                <span className="text-red-500 mx-3 mt-2">
                                    {error}
                                </span>
                            )}
                            <button
                                type="submit"
                                className={`mx-3 cursor-pointer text-amazon-blue_dark font-semibold bg-gradient-to-r hover:from-amazon-orange hover:to-yellow-300 hover:text-amazon-blue_dark  text-slate-100 hover:bg-gradient-to-r from-amazon-blue_light to-slate-500 w-full my-6 p-2 rounded-full transition duration-300`}
                            >
                                Apply
                            </button>

                            <div className="mx-3 flex flex-col">
                                <span>
                                    Total: <b>{cart.cartTotal}$</b>
                                </span>
                                {discount && (
                                    <span className="bg-green-600 p-2 my-2 text-white rounded-xl">
                                        Coupon applided: <b>-{discount}%</b>
                                    </span>
                                )}
                                {totalAfterDiscount < cart.cartTotal &&
                                    totalAfterDiscount != "" && (
                                        <span>
                                            New Price:{" "}
                                            <b>{totalAfterDiscount}$</b>
                                        </span>
                                    )}
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
            <button
                className={`mx-3 cursor-pointer text-amazon-blue_dark font-semibold bg-gradient-to-r from-amazon-orange to-yellow-300 text-amazon-blue_dark  hover:text-slate-100 hover:from-amazon-blue_light hover:to-slate-400 w-full my-6 p-4 rounded-full transition duration-300`}
                onClick={() => placeOrderHandler()}
            >
                Place Order
            </button>
            {order_error && (
                <span className="mx-3 text-red-500 mt-2">{order_error}</span>
            )}
        </div>
    );
};

export default Summary;
