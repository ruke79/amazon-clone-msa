import { useEffect, useState } from 'react';
import { emptyCart } from "../../redux/CartSlice";
import api from "util/api";

import { paymentMethods } from "../checkout/paymentMethods";
import { useDispatch } from "react-redux";
import * as PortOne from "@portone/browser-sdk/v2";

const Payment = ({ order, setLoading, setOrder }) => {
    const dispatch = useDispatch();

    const [paid, setPaid] = useState(null);

    useEffect(() => {
        
      }, []);


    const paymentHandler = async () => {
        try {
            setLoading(true);

  //           const { IMP } = window;

  //           const { IMP } = window;
  //   const buyerEmail = member ? member.memberEmail : '';
  //   const buyerName = member ? member.memberName : '';
  //   const buyerTel = member ? member.memberPhone : '';
  //   const name = order ? order.orderNo : '';
  //   const buyerAddr = order ? order.resipientAddr : '';
  //   const buyerPostcode = order ? order.resipientZipcode : '';
  //   const amount = order.orderPrice;

  //   IMP.init('imp11340204');

  //   IMP.request_pay({
  //     pg: 'kakaopay.TC0ONETIME',
  //     pay_method: 'card',
  //     merchant_uid: new Date().getTime(),
  //     name: name,
  //     amount: amount,
  //     buyer_email: buyerEmail,
  //     buyer_name: buyerName,
  //     buyer_tel: buyerTel,
  //     buyer_addr: buyerAddr,
  //     buyer_postcode: buyerPostcode,
  //   }, async (rsp) => {
  //     console.log('rsp: ', rsp);
  //     try {
  //       const { data } = await axios.post('/api/pay/verifyIamport/' + rsp.imp_uid);
  //       if (rsp.paid_amount === amount) {
  //         alert('결제 성공!');
  //         const testPay = {
  //           orderNo: order.orderNo,
  //           payPrice: amount,
  //           payStatus: rsp.success ? 'Y' : 'N'
  //         }

  //         //console.log('testPay.payStatus: ', testPay.payStatus);

  //         axios.post('/api/pay/createPayment', testPay, {
  //           headers: {
  //             'Content-Type': 'application/json',
  //           },
  //         })
  //           .then(response => {
  //             console.log('Pay data:', response.data);
  //             setPay(response.data);
  //           })
  //           .catch(error => {
  //             console.error('Error fetching pay data:', error);
  //           });

  //       } else if (rsp.paid_amount == amount) {
  //         alert('결제 성공?');
  //       } else {
  //         alert('결제 실패?');
  //       }
  //     } catch (error) {
  //       console.error('Error while verifying payment:', error);
  //       alert('결제 실패');
  //     }
  //   });
  // };


            // setTimeout(async () => {
                

            //     const { data } = await api.put(`/user/order/${order.id}/payment`, {
            //         id: order.id,
            //     });
            //     let paidOrder = {...order, isPaid : data }
            //     setOrder(paidOrder);
            //     dispatch(emptyCart(paidOrder));
                 setLoading(false);
            // }, 500);

        } catch (error) {
            setLoading(false);
            console.log("errr > ", error);
        }
    };

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
                                className={`cursor-pointer p-2 my-2 flex items-center rounded-xl ${
                                    order.paymentMethod == payment.id &&
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
                                            order.paymentMethod == payment.id
                                        }
                                    />
                                </label>
                                <div className="flex items-center ">
                                    <img
                                        src={`/../public/assets/images/${payment.id}.png`}
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
                <button
                    className=" mt-2 w-full rounded-xl bg-amazon-blue_light text-white p-4 font-semibold text-2xl hover:bg-amazon-blue_dark hover:scale-95 transition"
                    onClick={() => paymentHandler()}
                >
                    Pay
                </button>
            </div>
        </>
    );
};

export default Payment;
