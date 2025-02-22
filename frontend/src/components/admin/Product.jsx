import { Link } from "react-router-dom";

const AllProcuct = ({ products }) => {
     //console.log('products', products)

    return (
        <div className="my-4 ">
            <div className="flex p-2 border-b pb-1 font-semibold">
                All Product
            </div>
            <div className="flex flex-wrap">
                {products.map((product, i) => {
                    let created = new Date(product.createdAt);
                    return (
                        <div
                            className="flex flex-col w-56 m-2 p-2 border rounded hover:shadow hover:bg-slate-100 transition"
                            key={i}
                        >
                            <div className="relative w-52 h-72">
                                <img
                                    className="object-contained"                                    
                                    src={product.sku_products[0].images[0]}
                                    alt={product.name}
                                />
                            </div>
                            <Link href={`${process.env.PUBLIC_URL}/product/${product.slug}`} target="_blank">
                                <h3 className="text-sm font-semibold">
                                    {product.name}
                                </h3>
                            </Link>
                            <div className="mt-2 text-xs flex flex-col ">
                                <div>Sku: {product.sku_products[0].sku}</div>
                                <div>Category: {product.category.name}</div>
                                <div>
                                    Created:{" "}
                                    {`${created.getFullYear()}-${created.getMonth()}-${created.getDate()}`}
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default AllProcuct;
