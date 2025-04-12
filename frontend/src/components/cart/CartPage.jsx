import { useEffect, useState } from "react";
import CartHeader from "./CartHeader";
import Checkout from "./Checkout";
import PaymentMethods from "./PaymentMethods";
import Product from "./Product";
import api, { saveCart, putRequest, getRequest } from "util/api";
import { useLoaderData, useNavigate, useRouteLoaderData } from "react-router-dom";
import { useDispatch } from "react-redux";
import { commitCart, emptyCart, updateCart } from "../../redux/CartSlice";
import DotLoaderSpinner from "components/loader/Loading";
import { useAuthContext } from "store/AuthContext";
import { useQuery } from "@tanstack/react-query";
import tokenUtil from "util/tokenUtil";


const CartPage = ({ cart }) => {
    const { token, user } = useAuthContext();
    const dispatch = useDispatch();

    const [selected, setSelected] = useState([]);
    const [shippingFee, setShippingFee] = useState(0);
    const [subTotal, setSubTotal] = useState(0);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);
    const [enable, setEnable] = useState(cart.cartItems.length > 0);
    const [prevSelcted, setPrevSelectged] = useState(null);

    const navigate = useNavigate();



    useEffect(() => {

        if (cart.cartItems.length === 0)
            setEnable(false);
        else
            setEnable(true);

    }, []);

    useEffect(() => {
        setShippingFee(
            selected
                .reduce(
                    (total, product) =>
                        total + Number(product.shipping),
                    0
                )
                .toFixed(2)
        );
        setSubTotal(
            selected
                .reduce(
                    (total, product) =>
                        total + Number(product.price) * product.qty,
                    0
                )
                .toFixed(2)
        );
        setTotal(
            (
                selected.reduce(
                    (total, product) =>
                        total + Number(product.price) * product.qty,
                    0
                ) + selected.reduce(
                    (total, product) =>
                        total + Number(product.shipping),
                    0
                )
            ).toFixed(2)
        );

    }, [selected]);



    const saveCartToDbHandler = async (e) => {
        e.preventDefault();

        if (prevSelcted !== selected) {
            setPrevSelectged(selected);

            try {
            const res = await saveCart(selected, user.email);

            dispatch(commitCart(res.cartId));
            } catch(err) {
                console.error(err.response?.data.message);
            }
            finally {
                navigate("/checkout");
            }            
        }

    };

    return (
        <>
            {loading && <DotLoaderSpinner loading={loading} />}
            <div className="flex flex-col md:flex-row px-2 py-8 md:px-8 gap-4">
                <div className="md:w-3/4">
                    <CartHeader
                        cartItems={cart.cartItems}
                        selected={selected}
                        setSelected={setSelected}
                    />
                    <div className=" bg-white rounded py-2 px-4 border">
                        <h2 className="font-bold text-3xl my-2">
                            Shopping Cart
                        </h2>
                        <div className="w-full bg-slate-200 h-[1px]" />
                        {cart.cartItems.map((product, i) => (
                            <Product
                                product={product}
                                key={i}
                                selected={selected}
                                setSelected={setSelected}
                                cart={cart}
                            />
                        ))}
                    </div>
                </div>
                <div className="md:w-1/4">
                    <Checkout
                        subtotal={subTotal}
                        shippingFee={shippingFee}
                        total={total}
                        selected={selected}
                        saveCartToDbHandler={saveCartToDbHandler}
                    />
                    <PaymentMethods />
                </div>
            </div>
        </>
    );
};

export default CartPage;



