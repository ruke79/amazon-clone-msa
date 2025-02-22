import axios from "axios";
import slugify from "slugify";
import { prominent } from "color.js";
import { faker } from "@faker-js/faker";
import { convertURLtoFile, fetchImageFromURL, uploadImages } from "./imageUtil";
import dataURItoBlob from "./dataURItoBlob";
import api, { postRequest } from "./api";




let product = {
    name: "",
    description: "",
    brand: "",
    sku: "",
    discount: "",
    images: [],
    slug: "",

    parent: "",
    category: {
        name: "",
        slug: ""
    },
    subCategories: [
        {
            name: "",
            slug: "",
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

    createdAt: " "

};


let products = [];


const fetchColor = async (image) => {

    const result = await prominent(image, {
        format: "hex",
        amount: 6,
    })

    return result[getRandom(0, 5)];
}

function getRandom(min, max) {
    return Math.floor((Math.random() * (max - min + 1)) + min);
}

const loadFakeData = async () => {

    if (products.length == 0) {

        let formData = new FormData();

        await axios.get('https://fakestoreapi.com/products')
            .then(function (response) {
                let category, subcategory;

                const data = response.data;
                if (data.length > 0) {



                    for (let i = 0; i < data.length; i++) {


                        let p = data[i];


                        category = { name: p.category, slug: slugify(p.category) };
                        product.category = category;

                        product.subCategories = [];
                        subcategory = { name: p.category, slug: slugify(p.category) };
                        product.subCategories.push(subcategory);

                        product.name = p.title;
                        product.description = p.description;
                        product.brand = faker.company.name();
                        product.sku = p.category + p.id;
                        product.discount = getRandom(5, 70);
                        product.slug = slugify(product.name);

                        product.images = [];
                        product.images.push(p.image);

                        if (p.category === 'men\'s clothing' ||
                            p.category === 'women\'s clothing') {
                            product.sizes = [
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

                        product.color.colorImage = p.image;

                        products.push(structuredClone(product));

                    }

                }


            });


        const fectchImageArray = [fetchImageFromURL(products[0].color.colorImage),
        fetchImageFromURL(products[1].color.colorImage),
        fetchImageFromURL(products[2].color.colorImage),
        fetchImageFromURL(products[3].color.colorImage),
        fetchImageFromURL(products[4].color.colorImage),
        fetchImageFromURL(products[5].color.colorImage),
        fetchImageFromURL(products[6].color.colorImage),
        fetchImageFromURL(products[7].color.colorImage),
        fetchImageFromURL(products[8].color.colorImage),
        fetchImageFromURL(products[9].color.colorImage),
        fetchImageFromURL(products[10].color.colorImage),
        fetchImageFromURL(products[11].color.colorImage),
        fetchImageFromURL(products[12].color.colorImage),
        fetchImageFromURL(products[13].color.colorImage),
        fetchImageFromURL(products[14].color.colorImage),
        fetchImageFromURL(products[15].color.colorImage),
        fetchImageFromURL(products[16].color.colorImage),
        fetchImageFromURL(products[17].color.colorImage),
        fetchImageFromURL(products[18].color.colorImage),
        fetchImageFromURL(products[19].color.colorImage),
        ]

        Promise.all(fectchImageArray)
            .then((results) => {
                console.log(results);
                for (let i = 0; i < results.length; i++) {
                    formData.append("image", results[i]);
                    formData.append("colorImage", results[i]);
                }


                const fectchColorArray =  [fetchColor(products[0].color.colorImage),
                fetchColor(products[1].color.colorImage),
                fetchColor(products[2].color.colorImage),
                fetchColor(products[3].color.colorImage),
                fetchColor(products[4].color.colorImage),
                fetchColor(products[5].color.colorImage),
                fetchColor(products[6].color.colorImage),
                fetchColor(products[7].color.colorImage),
                fetchColor(products[8].color.colorImage),
                fetchColor(products[9].color.colorImage),
                fetchColor(products[10].color.colorImage),
                fetchColor(products[11].color.colorImage),
                fetchColor(products[12].color.colorImage),
                fetchColor(products[13].color.colorImage),
                fetchColor(products[14].color.colorImage),
                fetchColor(products[15].color.colorImage),
                fetchColor(products[16].color.colorImage),
                fetchColor(products[17].color.colorImage),
                fetchColor(products[18].color.colorImage),
                fetchColor(products[19].color.colorImage),
                ]
        
                Promise.all(fectchColorArray)
                    .then((results) => {
                        console.log(results);
                        for (let i = 0; i < results.length; i++) {                    
                            products[i].color.color = results[i];                    
                        }
                        try {
                            //const { response } = postRequest('catalog-service/api/products', { products });
                                                
                            formData.append("products", new Blob([JSON.stringify(products)], {
                                type: 'application/json'
                            }));
                            
                            const { response } = postRequest('catalog-service/api/products', formData,  
                                {
                                    headers: {
                                        "Content-Type": "multipart/form-data",
            
                                    },
                                    transformRequest: [
                                        function () {
                                            return formData;
                                        },
                                    ],
                                }
                            );
                        }
                        catch (err) {
        
                        }
                    });


            });


    }

}

export default loadFakeData;


