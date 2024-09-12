import axios from "axios";
import slugify from "slugify";
import { prominent } from "color.js";
import { faker } from "@faker-js/faker";
import { uploadImages } from "./uploadImages";
import dataURItoBlob from "./dataURItoBlob";
import { postRequest } from "./api";
















let product = {
    name: "",
    description: "",
    brand: "",
    sku: "",
    discount: "",
    images: [],
    slug : "",
    
    parent: "",
    category: {
        name :"",
        slug:""
    },
    subCategories: [
        {
            name : "",            
            slug : "",
        }
    ],
    color: {
        color: "",
        colorImage: "",
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

let uploaded_images = [];
    
let products = [];

function getRandom(min, max) {
     return Math.floor((Math.random() * (max - min+ 1)) + min);
    }
 
const loadFakeData = async() => {

    if (products.length == 0) {
    
        await axios.get('https://fakestoreapi.com/products')
        .then(function(response) {
            let category, subcategory;
                
            const data = response.data;
            if (data.length > 0) {
            
                

                for (let i = 0 ; i < data.length; i++) {

                    
                    let p = data[i];

                                
                    category = { name : p.category, slug : slugify(p.category)};                
                    product.category = category;
                    
                    product.subCategories = [];
                    subcategory = { name : p.category, slug : slugify(p.category)};
                    product.subCategories.push(subcategory);

                    product.name = p.title;
                    product.description = p.description;
                    product.brand = faker.company.name();
                    product.sku = p.category + p.id;
                    product.discount = getRandom(5,70);
                    product.slug = slugify(product.name);

                    product.images = [];
                    product.images.push(p.image);

                    if ( p.category === 'men\'s clothing' || 
                        p.category === 'women\'s clothing' ) {
                        product.sizes =  [
                            {
                                size: "Small",
                                quantity: "100",
                                price: p.price,
                            },
                            {
                                size: "Normal",
                                quantity: "100",
                                price: p.price,
                            },
                            {
                                size: "Large",
                                quantity: "100",
                                price: p.price,
                            },
                        ]            
                    }
                    else {
                        product.sizes = [
                            {
                                size: "",
                                quantity: "100",
                                price: p.price,
                            }
                        ]
                    };

                    product.details = [ 
                        {
                            name: "Product Detail Name 1",
                            value: "Product Detal Value 1",
                        },
                        {
                            name: "Product Detail Name 2",
                            value: "Product Detal Value 2",
                        },
                    ];
                    product.questions = [
                        {
                            question: "Product Question 1",
                            answer: "Product Answer 1",
                        },
                    ];

                    product.shippingFee = "5";


                    const config = {
                        onUploadProgress: progressEvent => {
                            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);                
                        }
                    };

                    // let imageUploader;

                    
                    //     if (p.image) {
                    //         let images = [p.image];

                    //         // let files = images.map((img) => {
                    //         //     return dataURItoBlob(img);
                    //         // });

                    //         console.log(images);
                    //         const path = "product images";
                    
                    //         imageUploader = images.map(async (file) => {
                    //             let formData = new FormData();
                    //             formData.append("folder", path);
                    //             formData.append("file", file);
                    //             formData.append("upload_preset", "nd7idl8b");
                    //             formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);                
                    
                    //             const image = await uploadImages(formData, config);
                    //             uploaded_images.push(image.url);            
                    //         });
                    //     }
                        

                    //         let temp = p.image;        
                    //         const colorLoader = async(temp) => {

                    //             console.log(temp);
                                
                    //             let path = "product style images";
                    //             let formData = new FormData();
                    //             formData.append("folder", path);
                    //             formData.append("file", temp);
                    //             formData.append("upload_preset", "nd7idl8b");
                    //             formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
                    //             let cloudinary_style_img = await uploadImages(formData, config);
                    //             const style_img = cloudinary_style_img.url;
                    //             //console.log("uploaded style image: ", style_img);
                    //         };
                        
                    //         colorLoader(temp);
                    //         axios.all(imageUploader, colorLoader).then(async () => {

                    //             const result = fetchColor(p.image);

                    //             console.log(result);
                    //         }
                    //     );
                    
                    
                    prominent(p.image, {
                        format: "hex",
                        amount: 6,
                    }).then((result) => {                                    
                        product.color.color = result[getRandom(0, 5)];            
                
                    }
                    )

                    product.color.colorImage = p.image;                                
                    
                    
                    
                    products.push(structuredClone(product));
                    
                }
                
            }
        });

        console.log(products.length);

        try {
            const { response } = await postRequest('admin/products', { products : products})
        
                

        }
        catch(err) {
            
        }
    }    

}

export default loadFakeData;


