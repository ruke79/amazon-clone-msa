import {
    CheckIcon,
    ChevronDownIcon,
    StarIcon,
} from "@heroicons/react/24/solid";
import { Tooltip } from "@mui/material";
import { useState } from "react";
import { useSearchParams } from "react-router-dom";

const HeadingFilter = ({
    priceHandler,
    multiPriceHandler,
    shippingHandler,
    replaceQuery,
    ratingHandler,
    sortHandler,
    searchParams
}) => {
    
    const [show, setShow] = useState(false);
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");



    const shippingParam = Number(searchParams.get('shipping'));
    let ratingParam = Number(searchParams.get('rating'));
    const priceParam  = searchParams.get('price');
    const sortParam  = searchParams.get('sort');
    

    const check = replaceQuery(
        "shipping",
        shippingParam === "0" ? false : "0"
    );
    const checkRating = replaceQuery("rating", "4");
    const sortQuery = sortParam || "Recomended";

    
    return (
        <div className="w-full flex flex-col md:flex-row md:items-end gap-x-6">
            <div>
                <span className="mr-2">Price:</span>
                <input
                    className="mx-1 w-20 rounded border py-1.5 px-2 outline-none"
                    type="number"
                    placeholder="min"
                    min="0"
                    onChange={(e) => {priceHandler(e.target.value, "min",500); setMinPrice(e.target.value) }}
                    value={minPrice || (priceParam)?.split("_")[0]}
                />
                <input
                    className="mx-1 w-20 rounded border py-1.5 px-2 outline-none"
                    type="number"
                    placeholder="max"
                    max="0"
                    onChange={(e) => {priceHandler(e.target.value, "max",500); setMaxPrice(e.target.value)  }}
                    value={maxPrice || (priceParam)?.split("_")[1] }
                />
            </div>

            <div>
                <Tooltip
                    title={<h2>Check out products under 10$</h2>}
                    placement="top"
                    arrow
                    onClick={() => multiPriceHandler(0, 10)}
                >
                    <button className="tooltip_btn">
                        <span style={{ height: "15%" }}></span>
                    </button>
                </Tooltip>
                <Tooltip
                    title={<h2>Check out products between 10$ and 50$</h2>}
                    placement="top"
                    arrow
                    onClick={() => multiPriceHandler(10, 50)}
                >
                    <button className="tooltip_btn">
                        <span style={{ height: "30%" }}></span>
                    </button>
                </Tooltip>
                <Tooltip
                    title={<h2>Check out products between 50$ and 100$</h2>}
                    placement="top"
                    arrow
                    onClick={() => multiPriceHandler(50, 100)}
                >
                    <button className="tooltip_btn">
                        <span style={{ height: "50%" }}></span>
                    </button>
                </Tooltip>
                <Tooltip
                    title={<h2>Check out products between 100$ and 500$</h2>}
                    placement="top"
                    arrow
                    onClick={() => multiPriceHandler(100, 500)}
                >
                    <button className="tooltip_btn">
                        <span style={{ height: "75%" }}></span>
                    </button>
                </Tooltip>
                <Tooltip
                    title={<h2>Check out products for more than 500$</h2>}
                    placement="top"
                    arrow
                    onClick={() => multiPriceHandler(500, "")}
                >
                    <button className="tooltip_btn">
                        <span style={{ height: "100%" }}></span>
                    </button>
                </Tooltip>
            </div>

            <div>
                <label
                    onClick={() => shippingHandler(check.result)}
                    htmlFor="shipping"
                    className="flex items-center cursor-pointer"
                >
                    <input
                        className="w-4 h-4 mr-1.5"
                        type="checkbox"
                        name="shipping"
                        id="shipping"
                        defaultChecked={shippingParam ==="0"}
                    />
                    Free Shipping
                </label>
            </div>

            <div>
                <label
                    onClick={() => ratingHandler(checkRating.result)}
                    htmlFor="rating"
                    className="flex items-center cursor-pointer"
                >
                    <input
                        className="mr-1 h-4 w-4"
                        type="checkbox"
                        name="rating"
                        id="rating"
                        defaultChecked={ratingParam === "4"}
                    />
                    <StarIcon className="h-5 w-5 fill-[#FACF19]" />
                    <StarIcon className="h-5 w-5 fill-[#FACF19]" />
                    <StarIcon className="h-5 w-5 fill-[#FACF19]" />
                    <StarIcon className="h-5 w-5 fill-[#FACF19]" />
                    <span className="ml-1">& up</span>
                </label>
            </div>

            <div
                className="dropdown_sortby ml-auto"
                onMouseOver={() => setShow(true)}
                onMouseLeave={() => setShow(false)}
            >
                <span>Sort by:</span>
                <button className="flex items-center ml-1">
                    {sortingOptions.find((x) => x.value === sortQuery)
                        ?.name || "Recommend"}{" "}
                    <ChevronDownIcon className="w-4 h-4 ml-2" />
                </button>
                {show && (
                    <ul>
                        {sortingOptions.map((option, i) => (
                            <li
                                key={i}
                                onClick={() => sortHandler(option.value)}
                                className={`flex justify-between ${
                                    sortQuery === option.value
                                        ? "font-semibold"
                                        : ""
                                }`}
                            >
                                <a>{option.name}</a>
                                {sortQuery === option.value ? (
                                    <CheckIcon className="w-5 h-5" />
                                ) : (
                                    ""
                                )}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default HeadingFilter;

const sortingOptions = [
    {
        name: "Recommend",
        value: "",
    },
    {
        name: "Most Popular",
        value: "popular",
    },
    {
        name: "New Arrivals",
        value: "newest",
    },
    {
        name: "Top Selling",
        value: "topSelling",
    },
    {
        name: "Top Reviewed",
        value: "topReviewed",
    },
    {
        name: "Price (Low to High)",
        value: "priceLowToHight",
    },
    {
        name: "Price (High to Low)",
        value: "priceHighToLow",
    },
];
