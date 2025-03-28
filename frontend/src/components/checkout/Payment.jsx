import { paymentMethods } from "./paymentMethods";
//import paypal from "assets/images/paypal.png"; 
//import credit_card from "assets/images/credit_card.png"; 
//import cash from "assets/images/cash.png"; 

const PaymentCheckout = ({ paymentMethod, setPaymentMethod, profile }) => {

    return (
        <>
            {!profile && (
                <h3 className=" pb-2 mb-4 border-b border-b-2  text-xl font-semibold">
                    Payment Method
                </h3>
            )}
            <div>
                {paymentMethods.map((payment) => (
                    
                    <div
                        key={payment.id}
                        className={`cursor-pointer p-2 my-2 flex items-center rounded-xl ${
                            paymentMethod == payment.id && "bg-slate-200"
                        } hover:bg-slate-200 transition`}
                        onClick={() => setPaymentMethod(payment.id)}
                    >
                        <label htmlFor={payment.id} className="">
                            <input
                                type="radio"
                                name="payment"
                                id={payment.id}
                                readOnly
                                checked={paymentMethod == payment.id}
                            />
                        </label>
                        <div className="flex items-center disabled ">                            
                            <img
                                src={`${process.env.PUBLIC_URL}/public/assets/images/${payment.id}.png`}                                
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
                ))}
            </div>
        </>
    );
};

export default PaymentCheckout;
