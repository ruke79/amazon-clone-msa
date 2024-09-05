import { useState, useEffect, lazy, Suspense } from 'react';
import { useLoaderData, json, defer } from 'react-router-dom';
import CarouselContainer from "components/home/CarouselContainer";
import CategoriesProduct from "./CategoryProducts";
import HomeProductSwiper from 'components/home/HomeProductSwiper';
import api, { getRequest } from '../../util/api';

const HomePage = () => {
   
   const  data  = useLoaderData();   
   const products = data.data;

  
          
        
    return (
        <main className="max-w-screen-2xl mx-auto bg-gray-100">            
        <CarouselContainer/>
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

    try {
        const response  = await getRequest("/admin/product/products");

        return response;
  } catch(err) {

  }
   
}
  