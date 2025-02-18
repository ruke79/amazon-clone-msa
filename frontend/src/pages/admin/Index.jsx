import CreateProduct from "../../components/admin/CreateProduct";
import { useEffect, useState } from "react";
import api, { getRequest } from "../../util/api";
import { loadCategories } from "../../components/admin/CreateCategory";

const initialState = {
    name: "",
    description: "",
    brand: "",
    sku: "",
    discount: 0,
    images: [],
    //description_images: [],
    parent: "",
    category: "",
    subCategories: [],
    color: {
        color: "",
        image: "",
    },
    sizes: [
        {
            size: "",
            quantity: "",
            price: "",
        },
    ],
    details: [
        {
            name: "",
            value: "",
        },
    ],
    questions: [
        {
            question: "",
            answer: "",
        },
    ],
    shippingFee: "",
};

const AdminProduct = () => {
    const [parents, setParents] = useState([]);    
    const [product, setProduct] = useState(initialState);
    const [subs, setSubs] = useState([]);
    const [categories, setCategories] = useState([]);
    const [colorImage, setColorImage] = useState("");
    const [images, setImages] = useState([]);
    //const [descriptionImages, setDescriptionImages] = useState("");
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        async function getProducts() {

            const { data } = await api.get("/admin/product/products");
            if(data) {
                setParents(data);
            }
        }

        getProducts();

    }, []);

    useEffect(() => {    
        async function getParentData() {            
            const { data } = await getRequest(`/catalog-service/api/product/${product.parent || ""}`);
            if(data) {  
                                                        
                setProduct({
                    ...product,
                    name: data.name,
                    description: data.description,
                    brand: data.brand,
                    category: data.category.id,
                    subCategories: Array.from(data.subCategories, (sub)=>sub['id']),
                    questions: data.questions,
                    details: data.details,
                    discount: data.discount
                })
            }
             
        };
        if (product.parent ) {
            getParentData();
        }
    }, [product.parent]);

    useEffect(() => {

        loadCategories(setCategories);

        

        async function getSubs() {

            
            const { data } = await getRequest("/admin/product/subcategories", {
                params: {
                    category : product.category,
                },
            });            
            setSubs(data);
        }
        if (product.category)
            getSubs();
    }, [product.category, product.name]);

    

    return (
        <>
            <CreateProduct
                parents={parents}
                product={product}
                setProduct={setProduct}
                categories={categories}
                subs={subs}
                images={images}
                setImages={setImages}
                setColorImage={setColorImage}
                colorImage={colorImage}
                setLoading={setLoading}
                loading={loading}
                initialProduct={initialState}
            />
        </>
    );
};

export default AdminProduct;

