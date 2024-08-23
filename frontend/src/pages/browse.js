import { filterArray, removeDublicates, randomize } from "util/array_utils";
import Header from "pages/header/Header";
import {Link, useLoaderData, useNavigate, useSearchParams } from "react-router-dom";
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
import api from "util/api";
// import DotLoaderSpinner from "@/components/loaders/dotLoader/DotLoaderSpinner";

const Browse = ({
    // categories,
    // subCategories,
    // products,
    // sizes,
    // colors,
    // brands,
    // styles,
    // materials,
    // paginationCount,
}) => {

    const loaderData = useLoaderData();

    let productsDB = JSON.parse(JSON.stringify(loaderData)).data;

   

    let categories = productsDB.categories;
    let subCategories = productsDB.subCategories;
    
    let products = productsDB.product.content;
    
    
    let details = productsDB.details;
    let brandsDb = productsDB.brandsDB;
    let paginationCount =  Math.ceil(productsDB.totalProducts / 10);
    let sizes = productsDB.sizes;
    let colors = productsDB.colors;

    
     let stylesDb = filterArray(details, "Style");
     let materialsDb = filterArray(details, "Material");
     let styles = removeDublicates(stylesDb);
     let materials = removeDublicates(materialsDb);
     let brands = removeDublicates(brandsDb);
    
     console.log(productsDB);
    
    
    const [searchParams, setSearchParams] = useSearchParams();
    // const [loading, setloading] = useState(false);
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
        //const path = router.pathname;
        function isEmptyObject(arg) {
            return typeof arg === 'object' && Object.keys(arg).length === 0;
          }
        
         function toggleParam(name, param) {
            if (param && !isEmptyObject(param)) {
                searchParams.set(name, param);
                setSearchParams(searchParams);
            }
            else if(isEmptyObject(param)) {
                searchParams.delete(name);
                setSearchParams(searchParams);            
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
        toggleParam('shipping', shipping);                     
        toggleParam('rating', rating);               
        toggleParam('sort', sort);                      
        toggleParam('page', page);                           
        
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
        // if (gender === "Unisex") {
        //     filter({ gender: {} });
        // } else {
        //     filter({ gender });
        // }
        filter({ gender });
    };

    // function throttle(fn, delay) {
    //     let lastInvoke = null;
    //     console.log('throttle',delay);

    //     return (...args[]) => {
    //         console.log('not invoke',args[0]);
    //         if (lastInvoke + delay < Date.now()) {
    //             console.log('invoke ', args[0]);
    //             lastInvoke = Date.now();
    //             fn(args[0]);
    //         }
    //     };
    // }

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

        const queryString = searchParams.toString();

        const existedQeury = searchParams.get(queryName);
        const valueCheck = existedQeury?.search(value);
        const _check = existedQeury?.search(`_${value}`);

        // console.log("queryName : " + queryName);
        // console.log("value : " + value);
        // console.log("existedQuery : " + existedQeury);
        // console.log("valueCheck" + valueCheck);
        
                
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
            {/* {loading && <DotLoaderSpinner loading={loading} />} */}
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
                        className={`h-[680px] col-span-5 md:col-span-1 flex flex-col md:items-center  overflow-y-auto overflow-x-hidden ${
                            scrollY >= height
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
                        className={`${
                            scrollY >= height ? "md:block" : "hidden"
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
                        />
                        <div className="mt-6 flex flex-wrap items-start gap-4">
                            {products.map((product) => (
                                <ProductCard
                                    product={product}
                                    key={product.productId}
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

export async function loader({request, params}) {

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
    const page = pageParam || 0;
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
    // const brand = brandQuery && brandQuery !== "" ? { brand: brandQuery } : {};
    const style =
        styleQuery && styleQuery !== ""
            ? styleSearchRegex : null;
    const size =
        sizeQuery && sizeQuery !== ""
            ? sizeSearchRegex  : {};
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
            ? genderSearchRegex  : {};
    const price =
        priceQuery && priceQuery !== ""
            ? {                
                lowPrice : Number(priceQuery[0]) || 0,
                highPrice : Number(priceQuery[1]) || 0,                
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

    const sort = "popular";
        // sortQuery == ""
        //     ? {}
        //     : sortQuery == "popular"
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


    let productsDb = await api.get("/search", 
        {params: { search, category, brand, style, 
            size, color, material, gender, lowPrice : price.lowPrice, highPrice : price.highPrice, shipping, rating, 
            page, pageSize, sort
        }}
    )

    console.log(productsDb);

    return productsDb;    
    
    // let products =
    //     sortQuery && sortQuery !== "" ? productsDb : randomize(productsDb);
    
    // let categories = await Category.find().lean();
    // let subCategories = await SubCategory.find()
    //     .populate({ path: "parent", model: Category })
    //     .lean();
    // let colors = await Product.find({ ...category }).distinct(
    //     "sku_products.color.color"
    // );
    // let brandsDb = await Product.find({ ...category }).distinct("brand");
    // let sizes = await Product.find({ ...category }).distinct(
    //     "sku_products.sizes.size"
    // );
    // let details = await Product.find({ ...category }).distinct("details");
    // let stylesDb = filterArray(details, "Style");
    // let materialsDb = filterArray(details, "Material");
    // let styles = removeDublicates(stylesDb);
    // let materials = removeDublicates(materialsDb);
    // let brands = removeDublicates(brandsDb);
    // let totalProducts = await Product.countDocuments({
    //     ...search,
    //     ...category,
    //     ...brand,
    //     ...style,
    //     ...size,
    //     ...color,
    //     ...material,
    //     ...gender,
    //     ...price,
    //     ...shipping,
    //     ...rating,
    // });

    // return {
    //     props: {
    //         categories: JSON.parse(JSON.stringify(categories)),
    //         products: JSON.parse(JSON.stringify(products)),
    //         subCategories: JSON.parse(JSON.stringify(subCategories)),
    //         sizes,
    //         colors,
    //         brands,
    //         styles,
    //         materials,
    //         paginationCount: Math.ceil(totalProducts / pageSize),
    //     },
    // };
}