import { MinusIcon, PlusIcon } from "@heroicons/react/24/solid";
import { useState } from "react";

const BrandsFilter = ({ brands, brandHandler, replaceQuery }) => {
    const [show, setShow] = useState(true);
    return (
        <div className="w-full">
            <h3
                onClick={() => setShow((prev) => !prev)}
                className={`cursor-pointer my-4 flex items-center justify-between font-semibold `}
            >
                Brands
                <span>
                    {show ? (
                        <MinusIcon className="w-5 h-5" />
                    ) : (
                        <PlusIcon className="w-5 h-5" />
                    )}
                </span>
            </h3>
            {show && (
                <div className="grid grid-cols-2 gap-3">
                    {brands.map((brand, i) => {
                        const check = replaceQuery("brand", brand);
                        return (
                            <button
                                key={i}
                                onClick={() => brandHandler(check.result)}
                                className={`${
                                    check.active ? "border-slate-500" : ""
                                } flex justify-center rounded border bg-white py-1 hover:border-slate-500`}
                            >
                                <img
                                    src={process.env.PUBLIC_URL + `/assets/images/${brand.toLowerCase()}.png`}
                                    width={50}
                                    height={50}
                                    alt={brand.toLowerCase()}
                                />
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default BrandsFilter;
