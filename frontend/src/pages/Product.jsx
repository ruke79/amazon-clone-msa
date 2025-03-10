import Footer from "./Footer";
import Header from "./header/Header";
import MenuSideBar from "./header/MenuSidebar";
import ProductPage from "../components/product/ProductPage";
import { useLoaderData, defer } from "react-router-dom";
import api, { getRequest } from "util/api";
import React,{ useEffect } from "react";

const SingleProduct = () => {
    
    const product = useLoaderData();
        
    return (
        <React.Fragment>
            product ? (
            <Header title={product.name} />
            <main className="bg-white w-full">
            <ProductPage product={product} />
            </main>    
            ) : <></>        
        </React.Fragment>
    );
};

export default SingleProduct;


export async function loader({request, params}) {
    const slug = params.slug;

    const searchParams = new URL(request.url).searchParams;
    const style = searchParams.get("style") || 0;
    const size = searchParams.get("size") || 0;
            
    //return await loaderProduct(slug, style, size);

    try {

        const  response = await getRequest(`/catalog-service/api/product/${slug}`);
    
                    
            const product = response.data;
    
                    
            const subProduct = product.skus[style];
        
            const prices = subProduct.sizes        
            .map((s) => s.price)
            .sort((a, b) => a - b);
          
            
            let newProduct = {
                ...product,
                style,
                images: subProduct.images,
                sizes: subProduct.sizes,
                discount: subProduct.discount,
                sku: subProduct.sku,
                colors: product.skus.map((p) => p.color),
                priceRange:
                    prices.discount > 1
                        ? `From ${(prices[0] - prices[0] / subProduct.discount).toFixed(
                              2
                          )} to ${(
                              prices[prices.length - 1] -
                              prices[prices.length - 1] / subProduct.discount
                          ).toFixed(2)} `
                        : `From ${prices[0]} to ${prices[prices.length - 1]}$`,
                price:
                    subProduct.discount > 0
                        ? (
                              subProduct.sizes[size].price -
                              subProduct.sizes[size].price / subProduct.discount
                          ).toFixed(2)
                        : subProduct.sizes[size].price,
                priceBefore: subProduct.sizes[size].price,
                quantity: subProduct.sizes[size].quantity,
                ratings: [
                
                ],
                allSizes: product.skus
                    .map((p) => p.sizes)
                    .flat()
                    .sort((a, b) => a.size - b.size)
                    .filter(
                        (element, index, array) =>
                            array.findIndex((el2) => el2.size === element.size) ===
                            index
                    ),
            };
            
           
            
            //return JSON.parse(JSON.stringify(newProduct));            
            return newProduct;            
            
    
        } catch(err) {
            console.error(err.response?.data.message)
            return null;
        }
    
    
    
}

async function loaderProduct(slug, style, size) {
    
   
   
   
    try {

    const  response = await getRequest(`/catalog-service/api/product/${slug}`);

                
        const product = response.data;

                
        const subProduct = product.skus[style];
    
        const prices = subProduct.sizes        
        .map((s) => s.price)
        .sort((a, b) => a - b);
      
        
        let newProduct = {
            ...product,
            style,
            images: subProduct.images,
            sizes: subProduct.sizes,
            discount: subProduct.discount,
            sku: subProduct.sku,
            colors: product.skus.map((p) => p.color),
            priceRange:
                prices.discount > 1
                    ? `From ${(prices[0] - prices[0] / subProduct.discount).toFixed(
                          2
                      )} to ${(
                          prices[prices.length - 1] -
                          prices[prices.length - 1] / subProduct.discount
                      ).toFixed(2)} `
                    : `From ${prices[0]} to ${prices[prices.length - 1]}$`,
            price:
                subProduct.discount > 0
                    ? (
                          subProduct.sizes[size].price -
                          subProduct.sizes[size].price / subProduct.discount
                      ).toFixed(2)
                    : subProduct.sizes[size].price,
            priceBefore: subProduct.sizes[size].price,
            quantity: subProduct.sizes[size].quantity,
            ratings: [
            
            ],
            allSizes: product.skus
                .map((p) => p.sizes)
                .flat()
                .sort((a, b) => a.size - b.size)
                .filter(
                    (element, index, array) =>
                        array.findIndex((el2) => el2.size === element.size) ===
                        index
                ),
        };
        
       
        
        //return JSON.parse(JSON.stringify(newProduct));            
        return newProduct;

    } catch(err) {
            
    }

}
