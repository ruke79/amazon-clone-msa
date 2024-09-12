import axios from "axios";
import slugify from "slugify";



const product = {
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

//npm install @faker-js/faker

let uploaded_images = [];
const loadImages = async(images) => {

    if (images) {

        let files = images.map((img) => {
            return dataURItoBlob(img);
        });

        const path = "product images";

        imageUploader = files.map(async (file) => {
            let formData = new FormData();
            formData.append("folder", path);
            formData.append("file", file);
            formData.append("upload_preset", "nd7idl8b");
            formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
            formData.append("timestamp", (Date.now() / 1000) | 0);


            const image = await uploadImages(formData, config);
            uploaded_images.push(image.url);            
        });
    }

    

    if (product.color.image) {
        let temp = dataURItoBlob(product.color.image);
        //formData.append("colorImage", temp);               
        let path = "product style images";
        let formData = new FormData();
        formData.append("folder", path);
        formData.append("file", temp);
        formData.append("upload_preset", "nd7idl8b");
        formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
        let cloudinary_style_img = await uploadImages(formData, config);
        style_img = cloudinary_style_img.url;
        console.log("uploaded style image: ", style_img);
        
    }
    axios.all(imageUploader).then(async () => {
        try {
            let formData = new FormData();

            formData.append("images", uploaded_images);
            formData.append("colorImage", style_img);


            product.slug = slugify(product.name);


            formData.append("product", new Blob([JSON.stringify(product)], {
                type: 'application/json'
            }));


            const { data } = await api.post("admin/product", formData,
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

function getRandom(min, max) {
     return Math.floor((Math.random() * (max - min+ 1)) + min);
    }
    
    
    

const loadFakeData = async() => {

    const { data } = await axios.get('https://api.escuelajs.co/api/v1/products');

    console.log(data);

    let category, subcategory;
    let products = [];

    for (const p in  data) {

        category = { name : p.category.name, slugify : slugify(p.category.name)};
        subcategory = { subcategoryName : p.category.name, parent : p.category.name,  slugify : slugify(p.category.name)};
        product.name = p.title;
        product.description = p.description;
        product.brand = faker.company.name();
        product.sku = p.category.name + p.id;
        product.discount = getRandom(5,70);

        if ( p.category.name === 'Clothes') {
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


        products.push(product);
        
    }

    
    


}

export default loadFakeData;


