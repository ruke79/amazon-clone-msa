import { useState, useEffect } from 'react';
import { useLoaderData, json, defer } from 'react-router-dom';
import CarouselContainer from "../../components/home/CarouselContainer";
import CategoriesProduct from "./CategoryProducts";
import HomeProductSwiper from 'components/home/HomeProductSwiper';
import api, { queryClient } from '../../util/api';
import { useMutation } from '@tanstack/react-query';



const getProductsData = async () => {
    const data  = await api.get("/admin/product/products");
    return data;
}

const HOME_QUERY_KEY = 'home';



const HomePage = () => {
    //const [products, setProducts] = useState([]);

    // const { data : products, mutate : loadProducts, isLoading, isSuccess,  isError } = useMutation({
    //     mutationFn : getProductsData, 
    //     onSuccess: (response) => {
           
    //         queryClient.invalidateQueries({ querykey: [HOME_QUERY_KEY] });            
    //     },
    //     onError: (error) => {
    //         console.log(error);
    //     }
    // });

    
    

    const  data  = useLoaderData();
    //const temp = JSON.parse(JSON.stringify(data));    
    const products = data.data;

        
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
    if(response.status != 200) {
  
        // throw json(
        //     { message: 'Could not fetch events.' },
        //     {
        //       status: 500,
        //     }
        //   );
        throw new Error('Could not fetch events.');
    }     
      
    return response;
  }
  