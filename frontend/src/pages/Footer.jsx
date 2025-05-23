import { GlobeAltIcon } from "@heroicons/react/24/outline";
import { Link } from "react-router-dom";

import amazonLogo from 'assets/images/amazon-logo.png';
import enFlag from "assets/images/en-flag.png";


const Footer = () => {
    
    return ( 
        <div className="flex flex-col w-full mx-auto">
            <Link to="#">
                <div className="flex bg-[#37475a] hover:bg-[#485769] justify-center">
                    <p className="text-white text-xs py-4">Back to top</p>
                </div> 
            </Link>

            <div className="grid grid-cols-2 lg:grid-cols-4  md:px-32 gap-4 px-5 bg-amazon-blue_light justify-center py-10 md:space-x-16 border-b border-slate-600">

                <div className="link-footer">
                    <h5>Get to Know Us</h5>
                    <ul>
                        <li><Link to="/">Blog</Link></li>
                        <li><Link to="/">About Amazon</Link></li>
                        <li><Link to="/">Investor Relations</Link></li>
                        <li><Link to="/">Amazon Devices</Link></li>
                        <li><Link to="/">Amazon Science</Link></li>
                    </ul>
                </div>

                <div className="link-footer">
                    <h5>Make Money with Us</h5>
                    <ul>
                        <li><Link to="/">Sell products on Amazon</Link></li>
                        <li><Link to="/">Sell on Amazon Business</Link></li>
                        <li><Link to="/">Sell apps on Amazon</Link></li>
                        <li><Link to="/">Become an Affiliate</Link></li>
                        <li><Link to="/">Advertise Your Products</Link></li>
                        <li><Link to="/">Self-Publish with Us</Link></li>
                        <li><Link to="/">Host an Amazon Hub</Link></li>
                        <li><Link to="/">See More Make Money with Us</Link></li>
                    </ul>
                </div>

                <div className="link-footer">
                    <h5>Amazon Payment Products</h5>
                    <ul>
                        <li><Link to="/">Amazon Business Card</Link></li>
                        <li><Link to="/">Shop with Points</Link></li>
                        <li><Link to="/">Investor Relations</Link></li>
                        <li><Link to="/">Reload Your Balance</Link></li>
                        <li><Link to="/">Amazon Currency Converter</Link></li>
                    </ul>
                </div>

                <div className="link-footer">
                    <h5>Let Us Help You</h5>
                    <ul>
                        <li><Link to="/">Amazon and COVID-19</Link></li>
                        <li><Link to="/">Your Account</Link></li>
                        <li><Link to="/">Your Orders</Link></li>
                        <li><Link to="/">Shipping Rates & Policies</Link></li>
                        <li><Link to="/">Returns & Replacements</Link></li>
                        <li><Link to="/">Manage Your Content and Devices</Link></li>
                        <li><Link to="/">Amazon Assistant</Link></li>
                        <li><Link to="/">Help</Link></li>
                    </ul>
                </div>

            </div>

            <div className="flex max-md:flex-col items-center bg-amazon-blue_light justify-center py-1">
                <img src={amazonLogo} alt="amazon-log" className="object-contain w-20 h-20 md:mr-20" />

                <div className="flex items-center space-x-2 max-md:mb-4">
                    <div className="flex space-around border rounded border-slate-400 px-2 py-2 text-xs text-slate-100 cursor-pointer">
                        <GlobeAltIcon className="h-4 mr-3" />
                        <span>English</span>
                    </div>
                    <div className="flex border rounded border-slate-400 px-2 py-2 text-xs text-slate-100 cursor-pointer">
                        <span className="text-slate-100 mr-3">$</span>
                        <span>USD - U.S. Dollar</span>
                    </div>
                    <div className="flex items-center border rounded border-slate-400 px-2 py-2 text-xs text-slate-100 cursor-pointer">
                        <img
                            src={enFlag}
                            alt="flag-country"
                            className="object-contain h-4 mr-3"
                        />
                        <span>United States</span>
                    </div>
                </div>
            </div>

            <div className="flex flex-col py-8 bg-[#131A22] items-center">

                <div className="mb-8">
                    <ul className="footer-link-services grid grid-cols-2 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-7 gap-8">
                        <li>
                            <Link to="">
                                <b>Mazon Music</b>
                                <p>Stream millions of songs</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>PillPack</b>
                                <p>Pharmacy Simplified</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Mazon Music</b>
                                <p>Stream millions of songs</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>PillPack</b>
                                <p>Pharmacy Simplified</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Mazon Music</b>
                                <p>Stream millions of songs</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>PillPack</b>
                                <p>Pharmacy Simplified</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Mazon Music</b>
                                <p>Stream millions of songs</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>               
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>PillPack</b>
                                <p>Pharmacy Simplified</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Amazon Global</b>
                                <p>Ship Orders Internationally</p>
                            </Link>
                        </li>
                        <li>
                            <Link to="">
                                <b>Home Services</b>
                                <p>Experienced Pros Happiness Guarantee</p>
                            </Link>
                        </li>
                    </ul> 
                </div>

                <div className="flex flex-col items-center">
                    <ul className="flex whitespace-nowrap max-md:flex-col items-center text-xs text-slate-300 space-x-4 max-md:space-y-2">
                        <li className="hover:underline">
                            <Link to="">Conditions of Use</Link>
                        </li>
                        <li className="hover:underline">
                            <Link to="">Privacy Notice</Link>
                        </li>
                        <li className="hover:underline">
                            <Link to="">Your Ads Privacy Choices</Link>
                        </li>
                    </ul>
                    <h6 className="text-xs text-slate-300 mt-1">
                        © 1996-2023, Amazon.com, Inc. or its affiliates
                    </h6>
                </div>
            </div>

        </div>
     );
}
 
export default Footer;