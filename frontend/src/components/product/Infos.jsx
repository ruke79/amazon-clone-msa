import { Rating } from "@mui/material";
import { useEffect, useState } from "react";
import {Link, useNavigate} from "react-router-dom";

import {
    HeartIcon,
    MinusIcon,
    PlusIcon,
    ShoppingBagIcon,
} from "@heroicons/react/24/outline";
import AccoridanProduct from "./AccordianProduct";
import api, { getRequest, putRequest } from "util/api";
import { useDispatch, useSelector } from "react-redux";
import { addToCart, emptyCart, updateCart } from "../../redux/CartSlice";
import { showDialog } from "../../redux/DialogSlice";
import { ArrowPathIcon } from "@heroicons/react/24/solid";
import { useAuthContext } from "../../store/AuthContext";
import { useSearchParams } from "react-router-dom";
import { useMutation, useQuery } from "@tanstack/react-query";


const getCartData = async({productId, style, size}) => {
    const { data }  = await getRequest(`/catalog-service/api/cart/${productId}?style=${style}&size=${size}`);    
    return data;
}



const Infos = ({ product, num_reviews, setActiveImg }) => {
    const { token } = useAuthContext();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    
    const dispatch = useDispatch();
    const { cartItems: cart } = useSelector((state) => state.cart);

    
        
    const sizeParam = searchParams.get('size');
    const styleParam = searchParams.get('style');    

    const [loading, setLoading] = useState(false);
    const [size, setSize] = useState(sizeParam);
    const [qty, setQty] = useState(1);
    const [error, setError] = useState("");

    const { mutate : saveCartOp } = useMutation({    
        mutationFn : getCartData, 
        onSuccess: (data) => {
    
            if (qty > data.quantity) {
                setError(
                    "the Quantity you have choosed is more than in stock. Try lower the Qty"
                );
                setLoading(false);
            } else if (data.quantity < 1) {
                setError("this Product is out of stock!");
                setLoading(false);
                return;
            } else {
                let _uid = `${product.name}_${product.style}_${sizeParam}`;
                                
                let exist = cart.find((p) => p._uid === _uid);
                if (exist) {
                    
                    let newCart = cart.map((p) => {
                        if (p._uid === exist._uid) {
                            return { ...p, qty: qty };
                        }
                        return p;
                    });
                    
                    dispatch(updateCart(newCart));
                    setError("");
                    setLoading(false);
                } else {       
                    
                    dispatch(addToCart({ ...data, qty, size: data.size, _uid }));
    
                    setError("");
                    setLoading(false);
                }
                
            }
        },
        
    });

    
    useEffect(() => {
        setSize("");
        setQty(1);
    }, [styleParam]);

    useEffect(() => {
        if (qty > product.quantity) {
            setQty(product.quantity);
        }
    }, [sizeParam]);

    const addToCartHandler = async () => {
        setLoading(true);
        if (!sizeParam) {
            setError("Please Select a size");
            setLoading(false);
            return;
        }
     
        saveCartOp({productId : product.id, style : product.style, size : sizeParam});
    };

    const handleWishlist = async () => {
        try {
            if (!token) {
                return navigate('/signin');
            }
            
            const { data } = await putRequest("/user/wishlist", {
                id: product.id,
                style: product.style,
            });
            dispatch(
                showDialog({
                    header: "Product added to whishlist successfully.",
                    msgs: [
                        {
                            msg: data.message,
                            type: "success",
                        },
                    ],
                })
            );
        } catch (error) {
            dispatch(
                showDialog({
                    header: "whislist Error",
                    msgs: [
                        {
                            msg: error.response.data.message,
                            type: "error",
                        },
                    ],
                })
            );
        }
    };

    return (
        <div className="flex flex-col row-span-3 md:col-span-3 max-md:px-2 mb-4">
            <h1 className="text-2xl font-bold ">{product.name}</h1>
            <div className="flex items-center ">
                <span className="cursor-pointer uppercase hover:underline text-sm mr-3 text-slate-600">
                    {product.brand}
                </span>
                <Rating
                    name="half-rating-read"
                    defaultValue={product.rating}
                    precision={0.5}
                    readOnly
                    style={{ color: "#FACF19" }}
                />
                <span className="text-slate-500">
                    {/* ({product.num_reviews}{" "}
                    {product.num_reviews >= 1 ? "reviews" : "review"}) */}
                    ({num_reviews}{" "}
                        {num_reviews >= 1 ? "reviews" : "review"})
                </span>
            </div>
            <div className="flex w-full bg-slate-200 h-[1px]" />

            <div className="mt-2 flex items-center">
                <div className="text-4xl font-semibold text-red-500">
                    {!size ? `${product.priceRange}` : `${product.price}$`}
                </div>

                {product.discount > 0 ? (
                    <div className="ml-2  text-slate-400">
                        {size && (
                            <span className=" text-xl line-through">
                                {product.priceBefore}
                            </span>
                        )}
                        <span className="ml-1 text-blue-500">
                            (-{product.discount}%)
                        </span>
                    </div>
                ) : (
                    ""
                )}
            </div>

            <div className="mt-1  text-sm text-slate-500">
                {size
                    ? product.quantity
                    : product.sizes.reduce(
                          (start, next) => start + next.quantity,
                          0
                      )}{" "}
                pieces Available
            </div>

            <p className=" mt-3  text-sm">{product.description}</p>

            <div className="mt-3 ">
                <h4 className="font-semibold text-slate-700">Select a Size:</h4>
                <div className="mt-2 flex gap-3 ">
                    {product.sizes.map((size, i) => (
                        <Link
                            key={i}
                            to={`/product/${product.slug}?style=${Number(styleParam)}&size=${i}`}
                        >
                            <div
                                onClick={() => {setSize(size.size);} }
                                className={`flex items-center justify-center w-11 h-11 rounded-full bg-slate-200 text-slate-700 hover:outline hover:outline-1 hover:outline-slate-400 hover:outline-offset-[3px] transition-all transition  ${
                                    i === Number(sizeParam) &&
                                    "font-semibold outline outline-1 outline-slate-400 outline-offset-[3px] bg-gradient-to-r from-amazon-orange to-slate-100"
                                }`}
                            >
                                {size.size}
                            </div>
                        </Link>
                    ))}
                </div>
            </div>

            <div className="mt-2 ">
                <h4 className="font-semibold text-slate-700">
                    Select a Color:
                </h4>
                <div className="mt-2 flex gap-3">
                    {product.colors &&
                        product.colors.map((color, i) => (
                            <span
                                key={i}
                                className={`rounded-full w-11 h-11 hover:outline outline-1 hover:outline-offset-[3px] hover:outline-slate-400  transition-all transition ${
                                    i === Number(styleParam)
                                        ? "outline outline-1 outline-offset-[3px] outline-slate-400"
                                        : ""
                                }`}
                                onMouseOver={() =>
                                    setActiveImg(
                                        product.skus[i].images[0]
                                    )
                                }
                                onMouseLeave={() => setActiveImg("")}
                            >
                                <Link
                                    to={`/product/${product.slug}/?style=${i}`}
                                >
                                    <img
                                        className="rounded-full object-cover h-11"
                                        width="44"
                                        height="44"
                                        src={color.colorImage}
                                        alt={color.color}
                                    />
                                </Link>
                            </span>
                        ))}
                </div>
            </div>

            <div className="mt-6  flex items-center space-x-2">
                <button
                    className="bg-slate-200 p-1.5 rounded-full hover:bg-slate-300"
                    onClick={() => qty > 1 && setQty((prev) => prev - 1)}
                >
                    <MinusIcon className="w-4 h-4  text-slate-800" />
                </button>
                <span className="text-m text-slate-900">{qty}</span>
                <button
                    className="bg-slate-200 p-1.5 rounded-full hover:bg-slate-300"
                    onClick={() =>
                        qty < product.quantity && setQty((prev) => prev + 1)
                    }
                >
                    <PlusIcon className="w-4 h-4  text-slate-900" />
                </button>
            </div>

            <div className="mt-2 flex flex-col md:flex-row md:space-x-3">
                <button
                    className={`flex flex-grow items-center justify-center bg-gradient-to-r from-amazon-orange to-yellow-300 text-amazon-blue_dark  p-2 rounded-full space-x-2 hover:text-slate-100 hover:from-amazon-blue_light hover:to-slate-500 hover:shadow-md transition duration-300 ${
                        product.quantity < 1 ? "cursor-not-allowed" : ""
                    }`}
                    disabled={product.quantity < 1}
                    onClick={() => addToCartHandler()}
                >
                    {loading ? (
                        <>
                            <ArrowPathIcon className="w-8 h-8" />
                            <span className="font-semibold text-xl">
                                Loading...
                            </span>
                        </>
                    ) : (
                        <>
                            <ShoppingBagIcon className="w-8 h-8" />
                            <span className="font-semibold text-xl">
                                ADD TO CART
                            </span>
                        </>
                    )}
                </button>

                <button
                    onClick={() => handleWishlist()}
                    className="flex items-center bg-slate-200 text-amazon-blue_light p-2 rounded space-x-2 hover:bg-amazon-blue_light hover:text-slate-100 transition duration-500 ease-in-out max-md:mt-3"
                >
                    <HeartIcon className="w-8 h-8" />
                    <span>WishList</span>
                </button>
            </div>
            <div className="m-2">
                {error && (
                    <span className="mt-2 text-red-500 font-semibold">
                        {error}
                    </span>
                )}
            </div>

            <div className="mt-4">
                <AccoridanProduct
                    details={product.details}
                    questions={product.questions}
                />
            </div>
        </div>
    );
};

export default Infos;
