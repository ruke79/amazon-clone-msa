import AllProduct from "components/admin/index";
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
        const response = await getRequest("/admin/product/products");

        return response;
    } catch (err) {        
        throw err;
    }

}
