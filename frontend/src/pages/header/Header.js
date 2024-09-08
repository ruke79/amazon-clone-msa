import { Link } from "react-router-dom";
import { Bars3Icon } from "@heroicons/react/24/outline";
import amazonLogo from "../../public/assets/images/amazon-logo.png";

import DeliveryTo from "./DeliveryTo";
import Search from "./Search";
import Account from './Account'
import HeaderMenu from "./HeaderMenu";


const Header = ({title, searchHandler}) => {

    const openMenuHandler = () => {
      
    };
    

    return (
        <>
        
                    
        <header>        
        <title>{title}</title>
            <meta name="description" content="full amazon clone React" />
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1"
            />           
            <div className="bg-amazon-blue_dark flex flex-col md:flex-row">
                <div className="flex flex-grow items-center p-3 md:space-x-5 md:px-4 text-white">
                    {/* Menu Icon Mobile */}
                    <div className="flex items-center justify-center">
                        <div
                            onClick={openMenuHandler}
                            className="md:hidden cursor-pointer mr-1"
                        >
                            <Bars3Icon className="h-8 md:h-7" />
                        </div>
                        {/* Logo */}
                        <Link to="/">
                            <img
                                src={amazonLogo}
                                alt="amazon-logo"
                                className="object-contain w-20 md:w-28 pt-2"
                            />
                        </Link>
                    </div>

                    <DeliveryTo />

                    {/* Search Desktop*/}
                    <div className="hidden md:flex flex-grow">
                        <Search searchHandler={searchHandler} />
                    </div>

                    {/* <Language /> */}
                    <Account />
                </div>

                {/* Search Mobile*/}
                <div className="md:hidden">
                    <Search />
                </div>
            </div>
            <link rel="icon" href="data:,"></link>
            <HeaderMenu handleOpenMenu={openMenuHandler} />
        </header>
    </>
    );

}

export default Header;