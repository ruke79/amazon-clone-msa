import { Link } from "react-router-dom"
import { useEffect, useState } from "react";
import ProductSwiper from "./ProductSwiper";

const ProductCard = ({ product }) => {

    
    const [active, setActive] = useState(0);
    const [images, setImages] = useState(product.skus[active]?.images);
    const [prices, setPrices] = useState(
        product.skus[active]?.sizes
            .map((s) => s.price)
            .sort((a, b) => a - b)
    );
    const [styles, setStyles] = useState(
        product.skus.map((p) => p.color)
    );

    useEffect(() => {
        setImages(product.skus[active]?.images);
        setPrices(
            product.skus[active]?.sizes
                .map((s) => s.price)
                .sort((a, b) => a - b)
        );
        
    }, [active]);
    
    return (
        <div className="flex flex-col relative w-[215px] rounded p-1">
            <Link to={`/product/${product.slug}?style=${active}${`${product.skus[active].sizes.length > 1 ? '&size='+ active : ''}`}`}>
                <ProductSwiper images={images} />
            </Link>
            {product.skus[active].discount > 0 && (
                <div className=" flex items-center justify-center absolute bg-yellow-400 w-9 h-9 rounded-full top-0 -right-1 text-sm z-50">
                    -{product.skus[active].discount}%
                </div>
            )}
            <div className=" mt-2">
                <Link to={`/product/${product.slug}?style=${active}`}>
                    <h3>
                        {product.name.length > 45
                            ? `${product.name.substring(0, 45)}`
                            : product.name}
                    </h3>
                </Link>
                <span className="text-xs text-red-500">
                    {prices.length === 1
                        ? `USD${prices[0]}$`
                        : `USD${prices[0]} - ${prices[prices.length - 1]}$`}
                </span>
                <div className="flex space-x-2 mt-1">
                    {styles &&
                        styles.map((style, i) =>
                            style.image ? (
                                <img
                                    key={i}
                                    src={style.image}
                                    className={`cursor-pointer h-[25px] object-cover rounded-full hover:outline hover:outline-black active:outline active:outline-black outline-1 outline-offset-2 ${
                                        i === active
                                            ? "outline outline-black"
                                            : ""
                                    }`}
                                    width={25}
                                    height={25}
                                    onMouseOver={() => {
                                        setImages(
                                            product.skus[i].images
                                        );
                                        setActive(i);
                                    }}
                                    alt={product.name}
                                />
                            ) : (
                                <span
                                    key={i}
                                    className={`cursor-pointer w-[25px] h-[25px] rounded-full hover:outline hover:outline-black active:outline active:outline-black outline-1 outline-offset-2 ${
                                        i === active
                                            ? "outline outline-black"
                                            : ""
                                    }`}
                                    style={{
                                        backgroundColor: `${style.color}`,
                                    }}
                                    onMouseOver={() => {
                                        setImages(
                                            product.skus[i].images
                                        );
                                        setActive(i);
                                    }}
                                ></span>
                            )
                        )}
                </div>
            </div>
        </div>
    );
};

export default ProductCard;
