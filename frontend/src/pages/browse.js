import { filterArray, removeDublicates, randomize } from "util/array_utils";
import Header from "pages/header/Header";
import { Link, useLoaderData, useNavigate, useSearchParams } from "react-router-dom";
import ProductCard from "components/home/ProductCard";
import { ChevronRightIcon } from "@heroicons/react/24/solid";
import CategoriesFilter from "components/browse/categoriesFilter/CategoriesFilter";
import SizesFilter from "components/browse/sizesFilter/SizesFilter";
import ColorsFilter from "components/browse/colorsFilter/ColorsFilter";
import BrandsFilter from "components/browse/brandsFilter/BrandsFilter";
import StylesFilter from "components/browse/stylesFilter/StylesFilter";
import MaterialsFilter from "components/browse/materialsFilter/MaterialsFilter";
import GenderFilter from "components/browse/genderFilter/GenderFilter";
import HeadingFilter from "components/browse/headingFilter/HeadingFilter";
import { Pagination } from "@mui/material";
import { useEffect, useRef, useState } from "react";
import api, { getRequest } from "util/api";
import keySort from "util/sort_utils";
import DotLoaderSpinner from "components/loader/Loading";

const Browse = ({ }) => {

    const productsDB = useLoaderData();



    let categories = productsDB.categories;
    let subCategories = productsDB.subCategories;

    let products = productsDB.product.content;

    console.log(productsDB);


    let details = productsDB.details;
    let brandsDb = productsDB.brandsDB;
    let paginationCount = Math.ceil(productsDB.totalProducts / 10);
    let sizes = productsDB.sizes;
    let colors = productsDB.colors;


    let stylesDb = filterArray(details, "Style");
    let materialsDb = filterArray(details, "Material");
    let styles = removeDublicates(stylesDb);
    let materials = removeDublicates(materialsDb);
    let brands = removeDublicates(brandsDb);


    const [searchParams, setSearchParams] = useSearchParams();
    const [loading, setloading] = useState(false);
    const navigate = useNavigate();


    const filter = ({
        search,
        category,
        brand,
        style,
        size,
        color,
        material,
        gender,
        price,
        shipping,
        rating,
        sort,
        page,
    }) => {

        function isEmptyObject(arg) {
            return typeof arg === 'object' && Object.keys(arg).length === 0;
        }

        function toggleParam(name, param) {
            if (param && !searchParams.get(param) && !isEmptyObject(param)) {
                searchParams.set(name, param);


                if (param !== 'page' && searchParams.get('page'))
                    searchParams.delete('page');

            }
            else if (param) {
                searchParams.delete(name);

            }
        }


        toggleParam('search', search);

        toggleParam('category', category);

        toggleParam('brand', brand);

        toggleParam('style', style);

        toggleParam('size', size);

        toggleParam('color', color);
        toggleParam('material', material);
        toggleParam('gender', gender);
        toggleParam('price', price);

        console.log("rating : " + rating);



        if (shipping && !searchParams.get('shipping')) {
            searchParams.set('shipping', shipping);

        }
        else if (shipping) {
            searchParams.delete('shipping');

        }

        if (rating && !searchParams.get('rating')) {

            searchParams.set('rating', rating);

        }
        else if (rating) {
            searchParams.delete('rating');

        }

        toggleParam('sort', sort);
        toggleParam('page', page);


        setSearchParams(searchParams);
    };

    const searchHandler = (search) => {
        if (search === "") {
            filter({ search: {} });
        } else {
            filter({ search });
        }
    };
    const categoryHandler = (category) => {
        filter({ category });
    };
    const brandHandler = (brand) => {
        filter({ brand });
    };
    const styleHandler = (style) => {
        filter({ style });
    };
    const sizeHandler = (size) => {
        filter({ size });
    };
    const colorHandler = (color) => {
        filter({ color });
    };
    const materialHandler = (material) => {
        filter({ material });
    };
    const genderHandler = (gender) => {

        filter({ gender });
    };


    function debounce(fn, delay) {
        let timeout = null;
        return (...args) => {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                fn(args[0]);
            }, delay);
        };
    }

    const priceHandler = (price, type, delay) => {
        let priceQuery = searchParams.get('price')?.split("_") || "";
        let min = priceQuery[0] || "";
        let max = priceQuery[1] || "";
        let newPrice = "";
        if (type === "min") {
            newPrice = `${price}_${max}`;
        } else {
            newPrice = `${min}_${price}`;
        }
        let filterPrice = debounce((price) => filter(price), delay);
        filterPrice({ price: newPrice });
    };

    const multiPriceHandler = (min, max) => {
        filter({ price: `${min}_${max}` });
    };

    const shippingHandler = (shipping) => {
        filter({ shipping });
    };
    const ratingHandler = (rating) => {
        filter({ rating });
    };
    const sortHandler = (sort) => {
        if (sort === "") {
            filter({ sort: {} });
        } else {
            filter({ sort });
        }
    };
    const pageHandler = (e, page) => {
        filter({ page });
    };

    const replaceQuery = (queryName, value) => {


        const existedQeury = searchParams.get(queryName);
        const valueCheck = existedQeury?.search(value);
        const _check = existedQeury?.search(`_${value}`);


        let result = null;
        if (existedQeury) {
            if (existedQeury === value) {
                result = {};
            } else {
                if (valueCheck !== -1) {
                    // if filtered value is in query & we want to remove it.
                    if (_check !== -1) {
                        // last
                        result = existedQeury?.replace(`_${value}`, "");

                    } else if (valueCheck === 0) {
                        // first
                        result = existedQeury?.replace(`${value}_`, "");

                    } else {
                        // middle
                        result = existedQeury?.replace(value, "");
                    }
                } else {
                    // if filtered value doesn't exist in Query & we wan to add it.
                    result = `${existedQeury}_${value}`;
                }
            }
        } else {
            result = value;
        }
        return {
            result,
            active: existedQeury && valueCheck !== -1 ? true : false,
        };
    };
    // ----------------------------------------
    const [scrollY, setScrollY] = useState(0);
    const [height, setHeight] = useState(0);
    const headerRef = useRef(null);
    const el = useRef(null);
    useEffect(() => {
        const handleScroll = () => {
            setScrollY(window.scrollY);
        };
        handleScroll();
        window.addEventListener("scroll", handleScroll);
        setHeight(
            headerRef.current?.offsetHeight + el.current?.offsetHeight + 50
        );

        return () => {
            {
                window.removeEventListener("scroll", handleScroll);
            }
        };
    }, []);

    return (
        <>
            {loading && <DotLoaderSpinner loading={loading} />}
            <Header title={"Browse Products"} searchHandler={searchHandler} />
            <div className="max-w-screen-2xl mx-auto bg-slate-100 p-1 md:p-6 gap-2">
                <div ref={headerRef}>
                    <div className="flex items-center text-sm">
                        <span className="text-slate-700">Home</span>
                        <ChevronRightIcon className="w-4 h-4 mx-1 fill-slate-600 " />
                        <span className="text-slate-700">Browse</span>
                        {searchParams.get('category') !== "" && (
                            <>
                                <ChevronRightIcon className="w-4 h-4 mx-1 fill-slate-600 " />
                                <span className="text-slate-700">
                                    {
                                        categories.find(
                                            (x) =>
                                                x.id === searchParams.get('category')
                                        )?.name
                                    }
                                </span>
                            </>
                        )}
                    </div>

                    <div
                        ref={el}
                        className="mt-2 flex flex-wrap gap-3 flex-wrap"
                    >
                        {categories.map((c) => (
                            <span
                                onClick={() => categoryHandler(c.id)}
                                className={`cursor-pointer flex items-center justify-center w-40 md:w-56 h-10 border bg-white rounded  transition-all duration-300 hover:bg-amazon-blue_light hover:text-white hover:scale-95 hover:border-amazon-blue_dark`}
                                key={c.id}
                            >
                                {c.name}
                            </span>
                        ))}
                    </div>
                </div>

                <div className="relative mt-4 grid grid-cols-5 gap-1 md:gap-5">
                    <div
                        className={`h-[680px] col-span-5 md:col-span-1 flex flex-col md:items-center  overflow-y-auto overflow-x-hidden ${scrollY >= height
                            ? "md:fixed md:w-[274px] md:top-2"
                            : ""
                            }`}
                    >
                        <button
                            onClick={() => navigate("/browse")}
                            className={`flex items-center justify-center w-56 md:w-full py-2 rounded transition-all duration-300 bg-amazon-blue_light text-white hover:scale-95 border-amazon-blue_dark`}
                        >
                            Clear All ({Array.from(searchParams).length})
                        </button>
                        <CategoriesFilter
                            categories={categories}
                            subCategories={subCategories}
                            categoryHandler={categoryHandler}
                            replaceQuery={replaceQuery}
                        />
                        <SizesFilter
                            sizes={sizes}
                            sizeHandler={sizeHandler}
                            replaceQuery={replaceQuery}
                        />
                        <ColorsFilter
                            colors={colors}
                            colorHandler={colorHandler}
                            replaceQuery={replaceQuery}
                        />
                        <BrandsFilter
                            brands={brands}
                            brandHandler={brandHandler}
                            replaceQuery={replaceQuery}
                        />
                        <StylesFilter
                            styles={styles}
                            styleHandler={styleHandler}
                            replaceQuery={replaceQuery}
                        />
                        <MaterialsFilter
                            materials={materials}
                            materialHandler={materialHandler}
                            replaceQuery={replaceQuery}
                        />
                        <GenderFilter
                            genderHandler={genderHandler}
                            replaceQuery={replaceQuery}
                        />
                    </div>

                    <div
                        className={`${scrollY >= height ? "md:block" : "hidden"
                            } max-md:hidden md:col-span-1`}
                    ></div>

                    <div className="col-span-5 md:col-span-4 flex flex-col content-start">
                        <HeadingFilter
                            priceHandler={priceHandler}
                            multiPriceHandler={multiPriceHandler}
                            shippingHandler={shippingHandler}
                            ratingHandler={ratingHandler}
                            sortHandler={sortHandler}
                            replaceQuery={replaceQuery}
                            searchParams={searchParams}
                        />
                        <div className="mt-6 flex flex-wrap items-start gap-4">
                            {products.map((product) => (
                                <ProductCard
                                    product={product}
                                    key={product.id}
                                />
                            ))}
                        </div>
                        <div className="w-full my-4 flex items-end justify-end">
                            <Pagination
                                count={paginationCount}
                                variant="outlined"
                                defaultPage={Number(searchParams.get('page')) || 1}
                                onChange={pageHandler}
                                size="large"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Browse;

export async function loader({ request, params }) {

    const searchParams = new URL(request.url).searchParams;
    const searchParam = searchParams.get("search");
    const categoryParam = searchParams.get("category");
    const priceParam = searchParams.get("price");
    const shippingParam = searchParams.get("shipping");
    const rateParam = searchParams.get("rate");
    const sortParam = searchParams.get("sort");

    const brandParam = searchParams.get("brand");
    const styleParam = searchParams.get("style");
    const sizeParam = searchParams.get("size");
    const colorParam = searchParams.get("color");
    const materialParam = searchParams.get("material");
    const genderParam = searchParams.get("gender");
    const pageParam = searchParams.get("page");

    const searchQuery = searchParam || "";
    const categoryQuery = categoryParam || "";
    const priceQuery = priceParam?.split("_") || "";
    const shippingQuery = shippingParam || 0;
    const ratingQuery = rateParam || "";
    const sortQuery = sortParam || "";
    const pageSize = 10;
    const page = pageParam || 1;
    // --------------------------------------------------
    const brandQuery = brandParam?.split("_") || "";
    const brandRegex = `^${brandQuery[0]}`;
    const brandSearchRegex = createRegex(brandQuery, brandRegex);
    // --------------------------------------------------
    const styleQuery = styleParam?.split("_") || "";
    const styleRegex = `^${styleQuery[0]}`;
    const styleSearchRegex = createRegex(styleQuery, styleRegex);
    // --------------------------------------------------
    const sizeQuery = sizeParam?.split("_") || "";
    const sizeRegex = `^${sizeQuery[0]}`;
    const sizeSearchRegex = createRegex(sizeQuery, sizeRegex);
    // --------------------------------------------------
    const colorQuery = colorParam?.split("_") || "";
    const colorRegex = `^${colorQuery[0]}`;
    const colorSearchRegex = createRegex(colorQuery, colorRegex);
    // --------------------------------------------------
    const materialQuery = materialParam?.split("_") || "";
    const materialRegex = `^${materialQuery[0]}`;
    const materialSearchRegex = createRegex(materialQuery, materialRegex);
    // --------------------------------------------------
    // --------------------------------------------------
    const genderQuery = genderParam?.split("_") || "";
    const genderRegex = `^${genderQuery[0]}`;
    const genderSearchRegex = createRegex(genderQuery, genderRegex);
    // --------------------------------------------------
    const search =
        searchQuery && searchQuery !== ""
            ? searchQuery : {};
    const category =
        categoryQuery && categoryQuery !== ""
            ? categoryQuery
            : {};
    console.log("categoryQuery: " + categoryQuery);
    // const brand = brandQuery && brandQuery !== "" ? { brand: brandQuery } : {};
    const style =
        styleQuery && styleQuery !== ""
            ? styleSearchRegex : null;
    const size =
        sizeQuery && sizeQuery !== ""
            ? sizeSearchRegex : {};
    const color =
        colorQuery && colorQuery !== ""
            ? colorSearchRegex : {};
    const brand =
        brandQuery && brandQuery !== ""
            ? brandSearchRegex : {};
    const material =
        materialQuery && materialQuery !== ""
            ? materialSearchRegex : {};
    const gender =
        genderQuery && genderQuery !== ""
            ? genderSearchRegex : {};
    const price =
        priceQuery && priceQuery !== ""
            ? {
                lowPrice: Number(priceQuery[0]) || 0,
                highPrice: Number(priceQuery[1]) || 0,
            }
            : {};
    const shipping =
        shippingQuery && shippingQuery === "0"
            ? {
                shipping: 0,
            }
            : {};
    const rating =
        ratingQuery && ratingQuery !== ""
            ? {
                rating: Number(ratingQuery),
            }
            : {};


    // --------------------------------------------------
    function createRegex(data, styleRegex) {
        if (data.length > 1) {
            for (let i = 1; i < data.length; i++) {
                styleRegex += `|^${data[i]}`;
            }
        }
        return styleRegex;
    }

    // console.log({category});
    // console.log({brand});
    // console.log({style});
    // console.log({size});
    // console.log({color});
    // console.log({material});
    // console.log({gender});
    // console.log({price});
    // console.log({shipping});
    // console.log({rating});

    try {

        let  response = await getRequest("/search",
            {
                params: {
                    search, category, brand, style,
                    size, color, material, gender, lowPrice: price.lowPrice, highPrice: price.highPrice, shipping: shipping.shipping, rating,
                    page, pageSize
                }
            }
        )
        
        let productsDB = JSON.parse(JSON.stringify(response)).data;



        if (sortQuery === "popular") {

            productsDB.sort(
                (a, b) => Number(b.rating) - Number(a.rating)
            );

            productsDB.map(product => {
                product.sku_products.sort(
                    (a, b) => Number(b.sold) - Number(a.sold)
                )
            });
        }
        else if (sortQuery === "newest") {
            productsDB.sort(
                (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
            );
        }
        else if (sortQuery === "topSelling") {

            productsDB.map(product => {
                product.sku_products.sort(
                    (a, b) => Number(b.sold) - Number(a.sold)
                )
            });
        }
        else if (sortQuery == "topReviewed") {
            productsDB.sort(
                (a, b) => Number(b.rating) - Number(a.rating)
            );

        }
        else if (sortQuery == "priceHighToLow") {
            productsDB.map(product => {
                product.sku_products
                    .map(sku => {
                        sku.sizes.sort(
                            (a, b) => Number(b.price) - Number(a.price)
                        )
                    })
            });

        }
        else if (sortQuery == "priceLowToHight") {
            productsDB.map(product => {
                product.sku_products
                    .map(sku => {
                        sku.sizes.sort(
                            (a, b) => Number(a.price) - Number(b.price)
                        )
                    })
            });

        }

        //     let sortOps = { rating : "DESC"}

        //     // console.log(products);
        //      products = products.keySort(sortOps);

        //      sortOps = { sold : "DESC"}
        //      products.map(product =>{product.sku_products.keySort(sortOps)});    
        // }
        //     ? { rating: -1, "sku_products.sold": -1 }
        //     : sortQuery == "newest"
        //     ? { createdAt: -1 }
        //     : sortQuery == "topSelling"
        //     ? { "sku_products.sold": -1 }
        //     : sortQuery == "topReviewed"
        //     ? { rating: -1 }
        //     : sortQuery == "priceHighToLow"
        //     ? { "sku_products.sizes.price": -1 }
        //     : sortQuery == "priceLowToHight"
        //     ? { "sku_products.sizes.price": 1 }
        //     : {};    
        console.log(productsDB);
        return productsDB;

    }
    catch(err) {
        throw err;

        
    }
        
}