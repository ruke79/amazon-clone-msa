//import Footer from "@/components/Footer";
import Header from "./header/Header";
//import MenuSideBar from "@/components/Header/MenuSidebar";
import ProductPage from "../components/product/ProductPage";
import { useLoaderData, defer } from "react-router-dom";
import api, { getRequest } from "util/api";

const SingleProduct = () => {
    
    const product = useLoaderData();
    
    

    return (
        <>
            <Header title={product.name} />
            <main className="bg-white w-full">
                <ProductPage product={product} />
            </main>
            {/* <Footer />
            <MenuSideBar /> */}
        </>
    );
};

export default SingleProduct;


export async function loader({request, params}) {
    const slug = params.slug;

    const searchParams = new URL(request.url).searchParams;
    const style = searchParams.get("style") || 0;
    const size = searchParams.get("size") || 0;
            
    return await loaderProduct(slug, style, size);
    
}

async function loaderProduct(slug, style, size) {
    
    
    
    //const  response = await api.get(`/product/${slug}`);

    try {

    const  response = await getRequest(`/product/${slug}`);

                
        const product = response.data;

        

        
        let subProduct = product.sku_products[style];
    
        let prices = subProduct.sizes        
        .map((s) => s.price)
        .sort((a, b) => a - b);
      
        
        let newProduct = {
            ...product,
            style,
            images: subProduct.images,
            sizes: subProduct.sizes,
            discount: subProduct.discount,
            sku: subProduct.sku,
            colors: product.sku_products.map((p) => p.color),
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
                {
                    percentage: calculatePercentage("5"),
                },
                {
                    percentage: calculatePercentage("4"),
                },
                {
                    percentage: calculatePercentage("3"),
                },
                {
                    percentage: calculatePercentage("2"),
                },
                {
                    percentage: calculatePercentage("1"),
                },
            ],
            allSizes: product.sku_products
                .map((p) => p.sizes)
                .flat()
                .sort((a, b) => a.size - b.size)
                .filter(
                    (element, index, array) =>
                        array.findIndex((el2) => el2.size === element.size) ===
                        index
                ),
        };
        
        function calculatePercentage(num) {
            return ( 
                product.reviews.length > 0 ? (product.reviews.reduce((total, review) => {
                    return (
                        total +
                        (review.rating === Number(num) ||
                            review.rating === Number(num) + 0.5)
                    );
                }, 0) *
                    100) /
                product.reviews.length : 0
            ).toFixed(1);
        }
        
        return JSON.parse(JSON.stringify(newProduct));            

    } catch(err) {
            throw err;
    }

}
