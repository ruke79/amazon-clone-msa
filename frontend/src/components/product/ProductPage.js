import {Link} from "react-router-dom";
import { useState } from "react";

import BreadCrumb from "./BreadCrumb";
import Infos from "./Infos";
import ShippingInfo from "./ShippingInfo";
import MainSwiper from "./MainSwiper";
import Reviews from "./reviews/Reviews";
import SimilarSwiper from "./SimilarSwiper";

const ProductPage = ({ product }) => {
    const [activeImg, setActiveImg] = useState("");
    // console.log("active: ", activeImg);
    

    return (
        <div className="w-full bg-white h-auto px-3 mb-6 md:px-2">
            <BreadCrumb
                category={product.category}
                subCategories={product.subCategories}
            />
            <div className="grid grid-row-8 md:grid-cols-8 gap-4">
                <MainSwiper images={product.images} activeImg={activeImg} />
                <Infos product={product} setActiveImg={setActiveImg} />
                <ShippingInfo product={product} />
            </div>

            <div className="mt-2 mx-auto w-full md:w-4/5 p-2 border rounded-lg">
                <SimilarSwiper />
            </div>
            <Reviews product={product} />
        </div>
    );
};

export default ProductPage;
