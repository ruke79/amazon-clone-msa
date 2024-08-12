import CarouselContainer from "./CarouselContainer";
import CategoriesProduct from "./CategoryProducts";

const HomePage = ({ products }) => {


    return (
        <main className="max-w-screen-2xl mx-auto bg-gray-100">
        <CarouselContainer />
         { <CategoriesProduct products={products} />
        /*<div className="z-10 relative">
            <HomeProductSwiper products={products} category="women clothing" />
            <HomeProductSwiper products={products} category="shoes" />
            <HomeProductSwiper products={products} category="Beauty" />
            <HomeProductSwiper products={products} category="Kids" />
        </div>  */}
        </main>
    )
}

export default HomePage;