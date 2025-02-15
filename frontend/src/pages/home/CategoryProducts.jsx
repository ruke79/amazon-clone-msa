import GridCategory from "./GridCategory";

const CategoriesProduct = ({ products}) => {
    return (
        <div className="relative grid md:grid-cols-4 grid-flow-row-dense gap-4 -mt-16 sm:-mt-32 md:-mt-48 lg:-mt-80 z-10 p-4 bg-gradient-to-t from-gray-100 to-transparent">
            <GridCategory category="women's clothing" products={products} gridCols={2}/>
            <GridCategory category="men's clothing" products={products} gridCols={2}/>
            <GridCategory category="electronics" products={products} gridCols={2}/>
            <GridCategory category="jewelery" products={products} gridCols={2}/>
        </div>
    );
};

export default CategoriesProduct;


