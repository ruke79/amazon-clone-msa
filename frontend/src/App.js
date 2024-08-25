import { Provider } from "react-redux";
import { useMemo } from "react";
import { PersistGate } from "redux-persist/integration/react";
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import RootPage from "./pages/Root";
import HomePage, {loader as productsLoader } from './pages/home/Home';
import RegisterPage from './pages/user/RegisterPage';
import SignInPage from './pages/user/SignInPage';
import DashboardLayout from './components/admin/DashboardLayout';
import { ContextProvider, useAuthContext } from "./store/AuthContext";
import { persistor, store } from "./redux/store";
import Categories from './pages/admin/Category';
import SubCategories from './pages/admin/SubCategory';
import AdminProduct from './pages/admin/Product';
import SingleProduct, {loader as productLoader } from "./pages/Product";
import Browse, { loader as browseLoader } from "pages/browse";
import Cart from "pages/cart";
import Checkout, { loader as loaderCart } from "pages/checkout";
import OrderPage,{ loader as loaderOrder } from "pages/order";


const AppRouter = () => {

  const authContext = useAuthContext();

const router = useMemo( () => createBrowserRouter([
  {

    path: '/',
    element: <RootPage />,
    children: [
      { index: true, 
        element: <HomePage />,
        loader: productsLoader,
      },
      { path: '/signin', element: <SignInPage /> },
      { path: '/register', element: <RegisterPage /> },      
    ]
  },
  { path: '/product/:slug',
    //id: 'slug',
    element: <SingleProduct/>,
    loader: productLoader,
    
  },  
  {
    path: '/browse', 
    element: <Browse/>,
    loader : browseLoader, 
  },
  {
    path: '/cart',
    element: <Cart />,
  },
  {
    path: '/checkout',
    element: <Checkout/>,
    loader : loaderCart(authContext),
  },
  {
    path: '/order/:id',
    element: <OrderPage/>,
    loader : loaderOrder(authContext),
  },
  {
      path: '/admin',
      element: <DashboardLayout />,
      children: [
        { path: 'category', element: <Categories /> },
        { path: 'subcategory', element: <SubCategories /> },
        { path: 'product', element: <AdminProduct /> }
      ]
    }
  ]), [authContext]);

  return <RouterProvider router={router} />          
};

function App() {


  return (
    <>
    <ContextProvider>
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>          
            <AppRouter />
        </PersistGate>
      </Provider>
      </ContextProvider>
    </>
  );
}

export default App;
