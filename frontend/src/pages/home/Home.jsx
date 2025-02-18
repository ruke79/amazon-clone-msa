import { useState, useEffect, lazy, Suspense } from 'react';
import { useLoaderData, json, defer, useNavigate } from 'react-router-dom';
import CarouselContainer from "components/home/CarouselContainer";
import CategoriesProduct from "./CategoryProducts";
import HomeProductSwiper from 'components/home/HomeProductSwiper';
import { getRequest, putRequest } from '../../util/api';
import MenuSideBar from 'pages/header/MenuSidebar';
import Footer from 'pages/Footer';
import MicroServices from './MicroServices';

const HomePage = () => {
   
   const  products  = useLoaderData()?.data;   
     
          
        
    return (
        <>
        <main className="max-w-screen-2xl mx-auto bg-gray-100">            
        <MicroServices/>
        <CarouselContainer/>
        {products && 
        <CategoriesProduct products={products} />                 
        }
        {products && 
        <div className="z-10 relative">
            <HomeProductSwiper products={products} category="women's clothing" />
            <HomeProductSwiper products={products} category="men's clothing" />
            <HomeProductSwiper products={products} category="electronics" />
            <HomeProductSwiper products={products} category="jewelery" />
        </div>                  
        }
        </main>          
          </>
    )
}

export default HomePage;



export async function loader() {
    
    try {
        const response  = await getRequest("/catalog-service/api/product/products");
        

        return response
  } catch(err) {
       //document.location.href = '/signin'
       
       return null
  }    
}
  