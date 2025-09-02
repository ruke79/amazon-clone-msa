import GridCategory from "./GridCategory";

const CategoriesProduct = ({ products, gridCols}) => {
    return (
        <div className="relative grid md:grid-cols-4 grid-flow-row-dense gap-4 -mt-16 sm:-mt-32 md:-mt-48 lg:-mt-80 z-10 p-4 bg-gradient-to-t from-gray-100 to-transparent">
            <GridCategory category="fashion" products={products} gridCols={gridCols}/>
            <GridCategory category="consumer electronics" products={products} gridCols={gridCols}/>
            <GridCategory category="books" products={products} gridCols={gridCols}/>
            <GridCategory category="food" products={products} gridCols={gridCols}/>
            <GridCategory category="furniture" products={products} gridCols={gridCols}/>            
        </div>
    );
};

export default CategoriesProduct;


