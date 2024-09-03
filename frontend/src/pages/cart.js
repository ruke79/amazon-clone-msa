import Header from "pages/header/Header";
//import MenuSideBar from "components/Header/MenuSidebar";
import CartPage from "components/cart/CartPage";
import Empty from "components/cart/Empty";
import { useSelector } from "react-redux";


const Cart = () => {
    const { cart } = useSelector((state) => ( state.cart ));
         
    return (
        <>
            <Header />
            <main className="w-full h-screen">
                {cart.cartItems.length > 0 ? (
                    <CartPage cart={cart}/>
                ) : (
                    <Empty />
                )}
            </main>
            {/* <MenuSideBar /> */}
        </>
    );
};

export default Cart;

