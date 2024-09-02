import { Provider } from "react-redux";
import { useMemo } from "react";
import { PersistGate } from "redux-persist/integration/react";
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import RootPage from "./pages/Root";
import HomePage, { loader as homeLoader } from './pages/home/Home';
import RegisterPage from './pages/user/RegisterPage';
import SignInPage from './pages/user/SignInPage';
import DashboardLayout from './components/admin/DashboardLayout';
import { ContextProvider, useAuthContext } from "./store/AuthContext";
import { persistor, store } from "./redux/store";
import Categories from './pages/admin/Category';
import SubCategories from './pages/admin/SubCategory';
import AdminProduct from './pages/admin/Product';
import SingleProduct, { loader as productLoader } from "./pages/Product";
import Browse, { loader as browseLoader } from "pages/browse";
import Cart from "pages/cart";
import Checkout, { loader as loaderCart } from "pages/checkout";
import OrderPage, { loader as loaderOrder } from "pages/order";
import Address, { loader as loaderAddress } from "pages/profile/address";
import Orders from "pages/profile/orders";
import Security from "pages/profile/security";
import Profile, { loader as loaderProfile } from "pages/profile/profile";
import Payment, { loader as loaderPayment } from "pages/profile/payment"
import ProtectedRoute from "components/ProtectedRoute";
import ResponseInterceptor from 'util/responseInterceptor';
import { Outlet } from 'react-router-dom';
import { ErrorBoundary } from "react-error-boundary";

const ErrorBoundaryLayout = () => (
  <ErrorBoundary>    
    <Outlet />
  </ErrorBoundary>
);

const AppRouter = () => {

  const authContext = useAuthContext();

  const router = useMemo(() => createBrowserRouter([
    {
      element: <ErrorBoundaryLayout />,
      children: [
        {
          path: '/',
          element: <RootPage />,
          children: [
            {
              index: true,
              element: <HomePage />,
              loader: homeLoader,
            },
            { path: 'signin', element: <SignInPage /> },
            { path: 'register', element: <RegisterPage /> },
          ]
        },
        {
          path: '/profile',
          element: <ProtectedRoute><Profile /></ProtectedRoute>,
          loader: loaderProfile(authContext),
          // 원인 파악..
          children: [

            // {
            //   path : 'orders',
            //   element : <Orders/>,
            // },
            //   {
            //     path : 'payment', 
            //     element : <Payment/>,
            //   },
            //   {
            //     path : 'security',
            //     element : <Security/>,
            //   },
            // {
            //   path : 'wishlist',
            //   element : 
            // }
          ]
        },
        {
          path: 'profile/address',
          element: <ProtectedRoute><Address /></ProtectedRoute>,
          loader: loaderAddress(authContext),
        },
        {
          path: 'profile/orders',
          element: <ProtectedRoute><Orders /></ProtectedRoute>,
        },
        {
          path: 'profile/payment',
          element: <ProtectedRoute><Payment /></ProtectedRoute>,
          loader: loaderPayment(authContext),
        },
        {
          path: 'profile/security',
          element: <ProtectedRoute><Security /></ProtectedRoute>,
        },
        {
          path: '/product/:slug',
          //id: 'slug',
          element: <ProtectedRoute><SingleProduct /></ProtectedRoute>,
          loader: productLoader,

        },
        {
          path: '/browse',
          element: <Browse />,
          loader: browseLoader,
        },
        {
          path: '/cart',
          element: <ProtectedRoute><Cart /></ProtectedRoute>,
        },
        {
          path: '/checkout',
          element: <ProtectedRoute><Checkout /></ProtectedRoute>,
          loader: loaderCart(authContext),
        },
        {
          path: '/order/:id',
          element: <ProtectedRoute><OrderPage /></ProtectedRoute>,
          loader: loaderOrder(authContext),
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
      ]
    }
  ]), [authContext]);

  return <RouterProvider router={router}/>  

};

function App() {


  return (
    <>
      
      <ContextProvider>         
        <ResponseInterceptor/>      
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <AppRouter/>            
          </PersistGate>
        </Provider>
      </ContextProvider>
      
    </>
  );
}


export default App;
