import { Formik, Form } from "formik";
import * as Yup from "yup";
import SelectInput from './SelectInput';
import MultipleSelect from './MultipleSelect';
import AdminInput from "./AdminInput";
import { useDispatch } from "react-redux";
import DialogModal from "./DialogModal";
import { showDialog } from "../../redux/DialogSlice";
import ImagesProduct from "./ProductImages";
import Colors from "./ProductColors";
import Style from "./ProductStyle";
import Sizes from "./ProductSizes";
import Questions from "./ProductQuestions";
import Details from "./ProductDetails";
import dataURItoBlob from "../../util/dataURItoBlob";
import { uploadImages, instance, } from "../../util/uploadImages";
import { useState } from "react";
import validateCreateProduct from '../../util/validation';
import api from "../../util/api";
import axios from "axios";
import slugify from "slugify";

const CreateProduct = ({
    parents,
    product,
    setProduct,
    categories,
    subs,
    images,
    setImages,
    setColorImage,
    colorImage,
    setLoading,
    loading,
    initialProduct,
}) => {
    const dispatch = useDispatch();
    const [colors, setColors] = useState([]);

    const validate = Yup.object({
        name: Yup.string()
            .required("please add a name")
            .min(10, "product name must between 10 and 300 characters")
            .max(300, "product name must between 10 and 300 characters"),
        brand: Yup.string().required("please add a brand"),
        category: Yup.string().required("please select atleast one category"),
        // subCategories: Yup.array().min(1, "please select atleast one sub category"),
        sku: Yup.string().required("please add a sku/number"),
        color: Yup.string().required("please add a color"),
        description: Yup.string().required("please add a description"),
    });
    const CreateProduct = async () => {
        let test = validateCreateProduct(product, images);
        if (test === "valid") {
            createProductHnadler();
        } else {
            dispatch(
                showDialog({
                    header: "Please follow our instructions.",
                    msgs: test,
                })
            );
        }
    };
    
    let uploaded_images = [];
    let style_img = "";
    const createProductHnadler = async () => {
        setLoading(true);
  
        
        let imageUploader;

        
        
        if (images) {            

            let files = images.map((img) => {
                return dataURItoBlob(img);
            });
                
            const path = "product images";
            
            imageUploader = files.map(async( file ) => {
                let formData = new FormData();    
                formData.append("path", path);
                formData.append("file", file);                                          
                formData.append("upload_preset", "nd7idl8b");
                formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
                formData.append("timestamp", (Date.now() / 1000) | 0);
                
                
                const image = await uploadImages(formData);                
                uploaded_images.push(image.url);            
            });                            
        }
        
        if (product.color.image) {
            let temp = dataURItoBlob(product.color.image);        
            //formData.append("colorImage", temp);               
            let path = "product style images";
            let formData = new FormData();            
            formData.append("path", path);
            formData.append("file", temp);
            formData.append("upload_preset", "nd7idl8b");
            formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
            let cloudinary_style_img = await uploadImages(formData);
            style_img = cloudinary_style_img.url;
            console.log("uploaded style image: ", style_img);

        }
        axios.all(imageUploader).then(async() => {
            try {
                let formData = new FormData();
                
                formData.append("images", uploaded_images);                        
                formData.append("colorImage", style_img);

                               
                product.slug = slugify(product.name);
                console.log(product);
                
                formData.append("product", new Blob([JSON.stringify(product)], { 
                    type: 'application/json'
                }));            

                //  product.sku_products.map(sku =>
                //     { sku.images.map((idx, image) => 
                //         {
                //             image = uploadImages[idx].url
                //         });
                //       sku.color.colorImage = style_img;                
                //     }
                //  );                 
                                        
                const { data } = await api.post("admin/product", formData,
                    {
                        headers: {
                        "Content-Type": "multipart/form-data",
                        //"Content-Type" : "application/json"
                            
                        },
                        transformRequest: [
                        function () {
                            return formData;
                        },
                        ],
                    }                              
                );
                console.log(data);
                                    
                // if (data.status === 200) {
                //     setProduct(initialProduct);
                //     setImages([]);
                //     setColorImage("");
                //     setColors([]);
                //     dispatch(
                //         showDialog({
                //             header: "post created.",
                //             msgs:[{
                //                 msg: data.message,
                //                 type: "success",
                //             }],
                //         })
                //     );
                    
                // }
                setLoading(false);
            } catch (error) {
                setLoading(false);
                console.log(error.message);
            }
        });
    }    
    const handleChange = (e) => {
        
        const { value, name } = e.target;        
        if (name === "subCategories") {            

            const data = typeof value === "string" ? value.split(",") : value;            
            setProduct({
                ...product,
                [name]: data,
                
            });   
            
            
        } else {
            setProduct({
                ...product,
                [name]: value,
            });
        }
    };

    return (
        <div className="my-4">
            <div className="flex p-2 border-b pb-1 font-semibold">
                Create a Product
            </div>

            <DialogModal />
            <Formik
                enableReinitialize
                initialValues={{
                    name: product.name,
                    brand: product.brand,
                    description: product.description,
                    category: product.category,
                    subCategories: product.subCategories,
                    parent: product.parent,
                    sku: product.sku,
                    discount: product.discount,
                    color: product.color.color,
                    imageInputFile: "",
                    styleInput: "",
                }}
                validationSchema={validate}
                onSubmit={() => {
                    CreateProduct();
                }}
            >
                {(formik) => (
                    <Form>
                        <ImagesProduct
                            name="imageInputFile"
                            header="Product Carousel Images"
                            text="Add Images"
                            images={images}
                            setImages={setImages}
                            setColorImage={setColorImage}
                        />
                        <div className="flex flex-col">
                            <div className="flex items-center space-x-2">
                                {product.color.image && (
                                    <div className="relative ">
                                        <img
                                            src={product.color.image}
                                            alt="color-image"
                                            //fill
                                            className="object-contain"
                                            width = "30"
                                        />
                                    </div>
                                )}
                                {product.color.color && (
                                    <span
                                        className="w-12 h-12 rounded-full shadow my-2"
                                        style={{
                                            backgroundColor: `${product.color.color}`,
                                        }}
                                    ></span>
                                )}
                            </div>
                            <Colors
                                name="color"
                                product={product}
                                setProduct={setProduct}
                                colorImage={colorImage}
                                setColors={setColors}
                                colors={colors}
                            />
                            <Style
                                name="styleInput"
                                product={product}
                                setProduct={setProduct}
                                colorImage={colorImage}
                            />
                            <SelectInput
                                name="parent"
                                value={product.parent}
                                label="Parent Product"
                                data={parents}
                                handleChange={handleChange}
                            />
                            <SelectInput
                                name="category"
                                value={product.category}
                                label="Category"
                                data={categories}
                                handleChange={handleChange}
                                disabled={product.parent}
                            />
                            {product.category && (
                                <MultipleSelect
                                    name="subCategories"
                                    value={product.subCategories}
                                    data={subs}
                                    label="Sub Categories"
                                    disabled={product.parent}
                                    handleChange={handleChange}
                                />
                            )}
                        </div>
                        <div className="flex flex-col">
                            <h3 className="p-2 border-b pb-1 font-semibold">
                                Basic info
                            </h3>
                            <AdminInput
                                type="text"
                                name="name"
                                label="Name"
                                placeholder="Product Name"
                                icon="category"
                                onChange={handleChange}
                            />
                            <AdminInput
                                type="text"
                                name="description"
                                label="Description"
                                placeholder="Product Description"
                                icon="category"
                                onChange={handleChange}
                            />
                            <AdminInput
                                type="text"
                                name="brand"
                                label="Brand"
                                placeholder="Product Brand"
                                icon="category"
                                onChange={handleChange}
                            />
                            <AdminInput
                                type="text"
                                name="sku"
                                label="Sku"
                                placeholder="Product Sku"
                                icon="category"
                                onChange={handleChange}
                            />
                            <AdminInput
                                type="text"
                                name="discount"
                                label="Discount"
                                placeholder="Product Discount"
                                icon="category"
                                onChange={handleChange}
                            />
                            <AdminInput
                                type="text"
                                name="shippingFee"
                                label="ShippingFee"
                                placeholder="Shipping Fee"
                                icon="category"
                                onChange={handleChange}
                            />
                            <Sizes
                                sizes={product.sizes}
                                product={product}
                                setProduct={setProduct}
                            />
                            <Details
                                details={product.details}
                                product={product}
                                setProduct={setProduct}
                            />
                            <Questions
                                questions={product.questions}
                                product={product}
                                setProduct={setProduct}
                            />
                        </div>
                        <button
                            disabled={loading}
                            type="submit"
                            className={`my-4 w-52 py-2 px-4 rounded text-white ${loading ? ' bg-slate-400 ' : 'bg-green-500'}`}
                        >
                            {loading ? 'Loading...' : 'Create Product'}
                        </button>
                    </Form>
                )}
            </Formik>
        </div>
    );
};

export default CreateProduct;