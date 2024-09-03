import {
    CheckIcon,
    ChevronUpIcon,
    MapPinIcon,
    MinusCircleIcon,
    PhoneIcon,
    PlusSmallIcon,
    UserIcon,
} from "@heroicons/react/24/outline";
import { selectShippingAddress, deleteAddress } from "util/api";
//import { changeActiveAddress, deleteAddress } from "../../request/users";

const ListShipping = ({
    visible,
    setVisible,
    addresses,
    setAddresses,
    user,
    profile
}) => {



      
    
    const changeActiveHandler = async (id) => {
                
        const res = await selectShippingAddress(id);        

        setAddresses(res);
    };
    const deleteHandler = async (id, e) => {
        e.stopPropagation();
        const res = await deleteAddress(id);
        setAddresses(res);
    };

    return (
        <>
            {addresses?.map((address) => (
                <div
                    className={`relative cursor-pointer p-4 mb-4 border border-slate-100 rounded-xl shadow-md hover:shadow-xl hover:border-white hover:scale-[101%] transition duration-300 ${
                        address.active &&
                        "border-l-4 border-l-amazon-blue_light hover:border-l-amazon-blue_light"
                    } `}
                    key={address.id}
                    onClick={() => changeActiveHandler(address.id)}
                >
                    {addresses.length > 1 ? (
                        <div
                            className="z-10 absolute top-2 right-2 text-slate-600 hover:text-red-500 hover:scale-110 transition"
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
                                src={profile ? user.user?.image : user.image}
                                alt={address._id}
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
                                className={`flex items-center text-amazon-blue_light font-semibold ${
                                    !address.active && "hidden"
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
                            <PlusSmallIcon className="h-6 w-6" /> Add New
                            Address
                        </span>
                    )}
                </button>
            </div>
        </>
    );
};

export default ListShipping;
