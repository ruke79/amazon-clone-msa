import {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';

const GridCategory = ({ category, products, gridCols}) => {
    const length = gridCols * gridCols;
     //let [imgSrc,setImgSrc] = useState('');
     //let base64_to_imgsrc = Buffer.from(products, "base64").toString()
    // //add the string to the state
    // setImgSrc(base64_to_imgsrc)
    // <img src={"data:image/jpeg;base64," + imgSrc} />        
 
    
    products.map(product => 
    product.sku_products[0].images.map(image => image = JSON.stringify(image))
    );    

    console.log(category)    ;
    const selectedProducts = products.filter( (product) => 
        ( category === product.category.name)).slice(0, length);
    
    
    

    return ( 
        <div className="flex flex-col bg-white border rounded p-2 justify-center items-center">
                <h3 className="font-bold my-2 uppercase">{category.replace("-"," ")}</h3>
                <div className={`h-full grid grid-cols-1 gap-4 m-1 `}>
                
                {selectedProducts.map((product) => (
                        <Link to={`/product/${product.slug}`} key={product.product_id}>
                            <div className={`relative`}>
                                                            
                                <img
                                    //  src={"data:image/jpeg;base64,"+product.sku_products[0].images[0]}                                    
                                    src={product.sku_products[0].images[0]}
                                    alt={product.name}                                    
                                    className="h-[420px]  object-cover rounded  "
                                />  
                            </div>
                            {/* {length > 1 && (<h4 className="text-xs mt-1">{product.name}</h4>)} */}
                            
                        </Link>
                    ))}
     

                </div>
                {length > 1 ? (
                    <h4 className="text-xs cursor-pointer hover:underline text-blue-500 my-2">See more</h4>
                ) : (
                    <h4 className="text-xs cursor-pointer hover:underline text-blue-500 my-2">Shop now</h4>
                )}
        </div>
     );
}
 
export default GridCategory;