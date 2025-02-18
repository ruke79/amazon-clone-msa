import AllProduct from "components/admin/Product";
import { useLoaderData } from "react-router-dom";
import { getRequest } from "util/api";

const Products = () => {

    const products = useLoaderData().data;
    console.log('products: ', products)

    return (
        <AllProduct products={products} />

    );
};

export default Products;



export async function loader(AuthContext) {

    try {
        const response = await getRequest("/catalog-service/api/product/products");

        return response;
    } catch (err) {        
        
    }

}
