import paypal from "assets/images/paypal.png"; 
import credit_card from "assets/images/credit_card.png"; 
import cash from "assets/images/cash.png"; 

import { useReducer, useEffect, useState } from "react";
import { cancelCart, emptyCart } from "../../redux/CartSlice";
import { postRequest } from "util/api";

import { paymentMethods } from "../checkout/paymentMethods";
import { useDispatch } from "react-redux";
import { PayPalButtons, usePayPalScriptReducer } from "@paypal/react-paypal-js";
import { useAuthContext } from "store/AuthContext";

function reducer(state, action) {
    switch (action.type) {
      case "PAY_REQUEST":
        return { ...state, loading: true };
      case "PAY_SUCCESS":
        return { ...state, loading: false, success: true };
      case "PAY_FAIL":
        return { ...state, loading: false, error: action.payload };
      case "PAY_RESET":
        return { ...state, loading: false, success: false, error: false };
    }
  }

const Payment = ({ order, setLoading, setOrder }) => {
    const reduxDispatch = useDispatch();

    const { user } = useAuthContext();
    const [ payment, setPayment ] = useState(null);
    
    const [{ isPending }, paypalDispatch] = usePayPalScriptReducer();
  // if state not dispacth is not a function error
    const [state, dispatch] = useReducer(reducer, {
    loading: true,
    error: "",
    success: "",
  });
  
         
  useEffect(() => {
    
    if (!order.trackingId) {
      dispatch({
        type: "PAY_RESET",
      });
    } else {
        
      paypalDispatch({
        type: "resetOptions",
        value: {
          "client-id": process.env.REACT_APP_PAYPAL_CLIENT_ID,
          currency: "USD",
        },
      });
      paypalDispatch({
        type: "setLoadingStatus",
        value: "pending",
      });
    }
  }, [order]);

    function createOrderHanlder(data, actions) {
        console.log(actions.order);

        return actions.order
          .create({
            purchase_units: [
              {
                amount: {
                  value: order.total,
                },
              },
            ],
          })
          .then((order_id) => {
             return order_id;
           }          
        );
      }

      // const onApprove = async (data) => {

        
        
    

      // }

      function onApproveHandler(data, actions) {

        const payment = {
          orderId : order.id,          
          trackingId : order.trackingId, 
          //paypalOrderId : details.id,
          //amounts : details.purchase_units.amount.value,
          paypalOrderId : "",
          amounts : null,
          couponName : order.couponApplied,
          orderStatus : order.orderStatus,  
          paymentStatus : order.paymentStatus,
          userEmail : user.email,
          createdTime : null
          //createdTime : details.create_time
        };
        
        dispatch({ type: "PAY_REQUEST" });
        const captureFunc =   actions.order.capture().then(
          (details) => {
            console.log('detail : ' + details.id);
            
            payment.paypalOrderId = details.id;
            payment.amounts = details.purchase_units[0].amount.value;
            payment.createdTime = details.create_time;     
            payment.paymentStatus = "COMPLETED";

            const completePayment = (payment) => {
              console.log(payment);
    
              let promise = new Promise(async function (resolve, reject) {        
                const data = await postRequest(            
                  '/order-service/api/order/payment/paypal',
                  payment
                );                
                  resolve(data);                         
                }
              );             
          
              return promise;         
            }

            completePayment(payment).then (res=> {
              
              order.orderStatus = "PAID";
              setOrder(order);
              reduxDispatch(emptyCart()); 

            dispatch({ type: "PAY_SUCCESS", payload: res });
        });
      });
       
        return captureFunc;      
      }

      function onCancelHandler(id) {

        order.paymentStatus = 'CANCELED';
        
        const payment = {
          orderId : order.id,          
          trackingId : order.trackingId, 
          paypalOrderId : id.orderID,
          amounts : order.total,
          couponName : order.couponApplied,
          orderStatus : order.orderStatus,  
          paymentStatus : order.paymentStatus,
          userEmail : user.email,
          createdTime : null //new Date()      
        };

        const cancelPayment = (payment) => {
          console.log(payment);

          let promise = new Promise(async function (resolve, reject) {        
            const data = await postRequest(            
              '/order-service/api/order/payment/paypal',
              payment
            );                
              resolve(data);                         
            }
          );             
      
          return promise;         
        }

        cancelPayment(payment).then (res=> {
          
          order.orderStatus = 'CANCELED';
          setOrder(order);
          reduxDispatch(cancelCart()); 
          
        }
        );
          

      }

      function onErroHandler(error) {
        console.log(error);
        
        
      }

    return (
        <>
            <h3 className=" pb-2 mb-4 border-b border-b-2  text-xl font-semibold">
                Payment
            </h3>
            <div>
                {paymentMethods.map((payment) => {
                    if (payment.id == order.paymentMethod) {
                        return (
                            <div
                                key={payment.id}
                                className={`cursor-pointer p-2 my-2 flex items-center rounded-xl ${order.paymentMethod === payment.id &&
                                    "bg-slate-200"
                                    } hover:bg-slate-200 transition`}
                            >
                                <label htmlFor={payment.id} className="">
                                    <input
                                        type="radio"
                                        name="payment"
                                        id={payment.id}
                                        readOnly
                                        defaultChecked={
                                            order.paymentMethod === payment.id
                                        }
                                    />
                                </label>
                                <div className="flex items-center ">
                                    <img
                                        src={`${process.env.PUBLIC_URL}/assets/images/${payment.id}.png`}
                                        
                                        alt={payment.name}
                                        width={40}
                                        height={40}
                                        className="mx-3"
                                    />
                                    <div className="flex flex-col">
                                        <span className="font-semibold">
                                            {payment.name}
                                        </span>
                                        <p className="text-sm text-slate-600">
                                            {payment.description}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        );
                    }
                })}

                {/* <div className=" mt-2 w-full rounded-xl bg-amazon-blue_light text-white p-4 font-semibold text-2xl hover:bg-amazon-blue_dark hover:scale-95 transition"> */}

                    {/* {order.paymentMethod === "paypal" &&
                     ( */}

                        <div>
                            {isPending ? (
                                <span>loading...</span>
                            ) :
                             (
                                <PayPalButtons
                                    createOrder={createOrderHanlder}
                                    onApprove={onApproveHandler}
                                    onCancel={onCancelHandler}
                                    onError={onErroHandler}
                                >   </PayPalButtons>
                            )}
                        </div>
                          {/* )                        
                          } */}
                      {/* { order.paymentMethod === "credit_card" && }      */}
                 {/* </div> */}
            </div>
        </>
    );
};

export default Payment;
