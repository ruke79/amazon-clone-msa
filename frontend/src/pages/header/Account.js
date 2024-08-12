import {
    ChevronDownIcon,
    ShoppingCartIcon,
    UserIcon,
} from "@heroicons/react/24/outline";
import { ChevronRightIcon } from "@heroicons/react/24/solid";
import { Link, useNavigate } from "react-router-dom";
import { useAuthContext } from "../../store/AuthContext";
import { useEffect } from "react";

const Account = () => {
    const navigate = useNavigate();

    const { token, setToken, currentUser, setCurrentUser, isAdmin, setIsAdmin } =
    useAuthContext();

    useEffect(() => {
        console.log(currentUser);
      }, [token]);

    const handleLogout = () => {
        localStorage.removeItem("JWT_TOKEN"); // Updated to remove token from localStorage
        localStorage.removeItem("USER"); // Remove user details as well
        localStorage.removeItem("CSRF_TOKEN");
        localStorage.removeItem("IS_ADMIN");
        setToken(null);
        setCurrentUser(null);
        setIsAdmin(false);
        navigate("/signin");
      };

    return (
        <div className="flex items-center max-md:ml-auto md:space-x-6 space-x-2">
        {/* account Icon in Mobile */}
        <div className=" md:hidden">
            <Link className="flex items-center" to="/signin">
                <p className="text-sm">Sign in</p>
                <ChevronRightIcon className="h-3 " />
                <UserIcon className="h-6" />
            </Link>
        </div>

        <div className="hidden md:inline link relative show-account p-1">
            <p className="text-xs text-slate-300">
                {/* Hello, {session ? session.user?.name : "sign in"} */}
            </p>
            <p className="flex font-bold text-sm">
                Account & Lists
                <ChevronDownIcon className="h-4 self-end ml-1" />
            </p>

            {/* popOver Account */}
            <div className="z-20 show-account-popup absolute w-96 -right-14 h-auto bg-white rounded-sm border shadow-md mt-1">
                <div className="absolute h-3 w-3 bg-white rotate-45 -mt-1 right-[3.85rem] "></div>
                {token ? (
                    <div className="flex items-center justify-between p-3 border-b pb-2">
                        <p className="text-xl text-amazon-blue_light">
                            Hi,{" "}
                            <Link to="/profile">
                                <b>{currentUser?.username}</b>
                            </Link>
                        </p>
                        <div className="flex space-x-2">
                            <Link to="/profile">
                            <div className="button-orange px-6 py-[0.3rem] text-sm text-gray-900">
                                Profile
                            </div>
                            </Link>
                            <button
                                onClick={() => handleLogout()}
                                className="button-orange px-2 py-[0.3rem] text-sm text-gray-900"
                            >
                                Sign Out
                            </button>
                        </div>
                    </div>
                ) :  
                (
                    <div className="flex flex-col items-center p-3 m-3 border-b pb-2">
                        <button
                             onClick={() => navigate('/signin')}
                            className="button-orange px-16 py-[0.3rem] text-sm text-gray-900"
                        >
                            Sign in
                        </button>
                        <p className="text-xs text-gray-900 mt-2">
                            New customer?{" "}
                            <Link
                                to="/register"
                                className="text-[#05a] hover:text-amazon-orange hover:underline"
                            >
                                start here
                            </Link>
                        </p>
                    </div>
                )}

                <div className="flex m-3">
                    <div className="flex flex-col w-1/2">
                        <h4 className="font-bold text-base text-black mb-2">
                            Your List
                        </h4>
                        <ul className="text-gray-900 text-xs">
                            <li>Create a list</li>
                            <li>Find a list or Registry</li>
                        </ul>
                    </div>

                    <div className="flex flex-col w-1/2 border-l pl-4">
                        <h4 className="font-bold text-base text-black mb-2">
                            Your Account
                        </h4>
                        <ul className="text-gray-900 text-xs">
                            <li>Account</li>
                            <li>Orders</li>
                            <li>Registry</li>
                            <li>Recommendations</li>
                            <li>Browsing History</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div className="link hidden md:inline">
            <p className="tex t-xs text-slate-300">Returns</p>
            <p className="font-bold text-sm">& Orders</p>
        </div>

        <div
            // onClick={() => router.push("/cart")}
            className="relative link flex items-center"
        >
            <span className="flex items-center justify-center absolute top-0 right-[0.44rem] md:right-8 bg-amazon-orange text-amazon-blue_dark font-semibold h-5 w-5 rounded-full">
                {/* {cart.length} */}
            </span>
            <ShoppingCartIcon className="h-10" />
            <p className="hidden md:inline font-bold mt-2 text-sm">Cart</p>
        </div>
    </div>




    );



}

export default Account;


