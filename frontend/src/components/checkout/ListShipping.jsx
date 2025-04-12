import { useEffect, useState } from "react";
import {
    CheckIcon,
    ChevronUpIcon,
    MapPinIcon,
    MinusCircleIcon,
    PhoneIcon,
    PlusIcon,
    UserIcon,
} from "@heroicons/react/24/outline";

import DotLoaderSpinner from "components/loader/Loading";
import { useAuthContext } from "store/AuthContext";
import { selectShippingAddress, deleteAddress } from "util/api";
import { useDispatch } from "react-redux";


const ListShipping = ({
    setLoading,
    selectedAddress,    
    addresses,
    invalidate,
    user,
    profile
}) => {

    const [visible, setVisible] = useState(addresses.length ? false : true);    
    const [prevAddressId, setPrevAddressId] = useState(null);


    const changeActiveHandler = async (id, e) => {

        e.stopPropagation();

        if (prevAddressId !== id) {
            setPrevAddressId(id);

            try {
                const res = await selectShippingAddress(id);


            } catch (err) {
                console.error(err.response?.data.message);
            }

            invalidate();
        }

    };
    const deleteHandler = async (id, e) => {
        e.stopPropagation();
        //setLoading(true);
        try {
            const res = await deleteAddress(id);



        } catch (err) {
            console.error(err.response?.data.message);
        }

        invalidate();


        //setLoading(false);

    };



    return (
        <div>
            {addresses && addresses.map((address) => (
                <div
                    className={`relative cursor-pointer p-4 mb-4 border border-slate-100 rounded-xl shadow-md hover:shadow-xl hover:border-white hover:scale-[101%] transition duration-300 ${address.active &&
                        "border-l-4 border-l-amazon-blue_light hover:border-l-amazon-blue_light"
                        } `}
                    key={address.id}
                    onClick={(e) => changeActiveHandler(address.id, e)}
                >
                    {addresses.length > 0 ? (
                        <div
                            className="z-10 absolute top-2 right-2 text-slate-600 hover:text-red-500 hover:scale-110 transition"
                            key={address.id}
                            onClick={(e) => deleteHandler(address.id, e)}
                        >
                            <MinusCircleIcon className="w-6 h-6" />
                        </div>
                    ) : (
                        ""
                    )}
                    <div className="grid grid-cols-2 justify-center">
                        <div className="mb-4">
                            <img
                                src={""}
                                alt={address.id}
                                width={60}
                                height={60}
                                className="rounded-full"
                            />
                        </div>
                        <div className="flex flex-col md:justify-center">
                            {/* <span className="flex items-center">
                                <UserIcon className="w-4 h-4 mr-1" />
                                {address.firstname.toUpperCase()}{" "}
                                {address.lastname.toUpperCase()}
                            </span>
                            <span className="flex items-center">
                                <PhoneIcon className="w-4 h-4 mr-1" />
                                {address.phoneNumbe.toUpperCase()}
                            </span> */}
                        </div>
                    </div>
                    <div className="flex flex-col">
                        <div>
                            <span className="flex items-center">
                                <MapPinIcon className="w-5 h-5 mr-1" />
                                {address.address1}
                            </span>
                            {address.address2 && (
                                <span className="flex items-center">
                                    <MapPinIcon className="w-5 h-5 mr-1" />
                                    {address.address2}
                                </span>
                            )}
                        </div>
                        <span>
                            {address.city},{address.state},{address.country}
                        </span>
                        <div className="flex justify-between">
                            <span>{address.zipCode}</span>
                            <span
                                className={`flex items-center text-amazon-blue_light font-semibold ${!address.active && "hidden"
                                    }`}
                            >
                                <CheckIcon className="w-5 h-5 " /> Active
                            </span>
                        </div>
                    </div>
                </div>
            ))}

            <div className="flex justify-center">
                <button
                    onClick={() => setVisible((prev) => !prev)}
                    className={`flex justify-center w-52 my-4  py-4 rounded-xl hover:text-amazon-blue_dark font-bold bg-gradient-to-r hover:from-amazon-orange hover:to-yellow-300 hover:text-amazon-blue_dark  text-slate-100 from-amazon-blue_light to-slate-500 transition duration-300 hover:scale-95 `}
                >
                    {visible ? (
                        <span>
                            <ChevronUpIcon className="h-6 w-6" />
                        </span>
                    ) : (
                        <span className="flex items-center">
                            <PlusIcon className="h-6 w-6" /> Add New Address
                        </span>
                    )}
                </button>
            </div>
        </div>
    );
};

export default ListShipping;
