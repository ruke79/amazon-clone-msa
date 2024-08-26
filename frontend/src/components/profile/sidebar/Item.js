import { MinusIcon, PlusIcon } from "@heroicons/react/24/solid";
import { useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLongRightIcon } from "@heroicons/react/24/solid";
import slugify from "slugify";
import { useAuthContext } from "store/AuthContext";
import { useSearchParams, useNavigate } from "react-router-dom";

const Item = ({ item, visible, index }) => {
    const { token, setToken, currentUser, setCurrentUser, isAdmin, setIsAdmin } =
    useAuthContext();

    const [show, setShow] = useState(visible);
    const navigate = useNavigate();

    const { searchParams } = useSearchParams();
    const qParam = searchParams.get('q');

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
        <li className="py-2 cursor-pointer transition-all duration-300 text-amazon-blue_light">
            {item.heading == "Sign Out" ? (
                <b className="hover:text-[#fda22c]" onClick={() => handleLogout()}>
                    Sign Out
                </b>
            ) : (
                <b
                    className=" hover:text-[#fda22c] flex items-center justify-between transition-all"
                    onClick={() => setShow((prev) => !prev)}
                >
                    <span className={`${show && "text-[#fda22c]"}`}>
                        {item.heading}
                    </span>
                    {show ? (
                        <MinusIcon className="w-5 h-5 fill-[#fda22c]" />
                    ) : (
                        <PlusIcon className="w-5 h-5 mr-2" />
                    )}
                </b>
            )}
            {show && (
                <ul className="ml-2">
                    {item.links.map((link, i) => (
                        <>
                            {link.link.startsWith("/profile/orders") ? (
                                <li
                                    className={`${
                                        (qParam?.split("__")[0] || "") == slugify(link.name, {lower: true}) ? "text-black font-semibold" : "text-slate-600"
                                    } flex items-center  py-1 hover:text-[#fda22c] hover:font-semibold transition-all`}
                                    key={i}
                                >
                                    <ArrowLongRightIcon className="w-5 h-5 mr-2" />
                                    <Link
                                        to={`${
                                            link.link
                                        }?tab=${index}&q=${slugify(link.name, {
                                            lower: true,
                                        })}__${link.filter}`}
                                    >
                                        {link.name}
                                    </Link>
                                </li>
                            ) : (
                                <li
                                    className={`${
                                        (qParam || "") ==
                                        slugify(link.name, { lower: true })
                                            ? "text-black font-semibold"
                                            : "text-slate-600"
                                    } flex items-center  py-1 hover:text-[#fda22c] hover:font-semibold transition-all`}
                                    key={i}
                                >
                                    <ArrowLongRightIcon className="w-5 h-5 mr-2" />
                                    <Link
                                        to={`${
                                            link.link
                                        }?tab=${index}&q=${slugify(link.name, {
                                            lower: true,
                                        })}`}
                                    >
                                        {link.name}
                                    </Link>
                                </li>
                            )}
                        </>
                    ))}
                </ul>
            )}
        </li>
    );
};

export default Item;
