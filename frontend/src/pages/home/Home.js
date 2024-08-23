import { useState, useEffect } from 'react';
import { useLoaderData, json } from 'react-router-dom';
import CarouselContainer from "../../components/home/CarouselContainer";
import CategoriesProduct from "./CategoryProducts";
import HomeProductSwiper from 'components/home/HomeProductSwiper';
import api from '../../util/api';

const HomePage = () => {
    //const [products, setProducts] = useState([]);

    const data = useLoaderData();
    //const temp = JSON.parse(JSON.stringify(data));    
    const products = data.data;

    console.log(products);
    
    return (
        <main className="max-w-screen-2xl mx-auto bg-gray-100">
        <CarouselContainer />
         <CategoriesProduct products={products} />
        <div className="z-10 relative">
            <HomeProductSwiper products={products} category="women clothing" />
            <HomeProductSwiper products={products} category="shoes" />
            <HomeProductSwiper products={products} category="Beauty" />
            <HomeProductSwiper products={products} category="Kids" />
        </div>  
        </main>
    )
}

export default HomePage;


export async function loader() {

    const response  = await api.get("/admin/product/products");
    if(!response) {
  
        throw json(
            { message: 'Could not fetch events.' },
            {
              status: 500,
            }
          );
    }    
      
    return response;
  }
  