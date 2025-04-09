import { Provider } from "react-redux";
import { useMemo, Suspense, useEffect, useState, useCallback  } from "react";
import { PersistGate } from "redux-persist/integration/react";
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import RootPage from "./pages/Root";
import HomePage, { loader as homeLoader } from './pages/home/Home';
import RegisterPage from './pages/user/RegisterPage';
import SignInPage from './pages/user/SignInPage';
import DashboardLayout from './components/admin/DashboardLayout';
import { ContextProvider, useAuthContext } from "./store/AuthContext";
import { persistor, store } from "./redux/store";
// import Categories from './pages/admin/Category';
// import AdminProduct from './pages/admin/Index';
import SingleProduct, { loader as productLoader } from "./pages/Product";
import Browse, { loader as browseLoader, loader } from "pages/browse";
import Cart, { loader as loaderCart } from "pages/cart";
import Checkout, { loader as loaderCheckout } from "pages/checkout";
import OrderPage, { loader as loaderOrder } from "pages/order";
import Address, { loader as loaderAddress } from "pages/profile/address";
import Orders, { loader as loaderOrders } from "pages/profile/orders";
import Security, {loader as loaderSecurity } from "pages/profile/security";
import Profile, { loader as loaderProfile } from "pages/profile/profile";
import Payment, { loader as loaderPayment } from "pages/profile/payment"


import ProtectedRoute from "components/ProtectedRoute";
import { ErrorBoundary } from "react-error-boundary";
import { Outlet  } from 'react-router-dom';
import ErrorPage from "pages/Error";
import { QueryClientProvider } from '@tanstack/react-query'
import { queryClient } from 'util/api';
import Maintenance from 'components/error/Maintenance';
import Products, { loader as loaderProducts }  from "pages/admin/ProductList";
import { PayPalScriptProvider, PayPalButtons } from "@paypal/react-paypal-js";
import TokenRefresher from "util/TokenRefresher";
import toast, {Toaster} from 'react-hot-toast';
import { ChevronUpIcon } from "@heroicons/react/24/outline";
import loadFakeData from "util/loadFake";
import { postRequest, getRequest } from "util/api";
import { useOnMounted } from "hook/useOnMounted";
import { jwtDecode } from "jwt-decode";
import ChatRoomList from "components/chat/list/ChatRoomList";
import { ChatPage } from "pages/chat/ChatPage";
import Chat from "pages/chat/Index";



// const DebugLayout = () => {
//   const location = useLocation();
//   const navigationType = useNavigationType(); // "POP" | "PUSH" | "REPLACE"

//   useEffect(() => {
//     console.log("The current URL is", {...location});
//     console.log("The last navigation action was", navigationType);
//   }, [location, navigationType]);

//   return <Outlet />;
// };


function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <p>{error}</p>
      {/* <p>{error.code}</p> */}
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

const initialOptions = {
  "client-id": process.env.REACT_APP_PAYPAL_CLIENT_ID,
  currency: "USD",
  intent: "capture",
  components: 'buttons'
};

const ErrorBoundaryLayout = () => (
  
//<Suspense fallback={<div>Loading...</div>}>
   <ErrorBoundary FallbackComponent = {ErrorFallback} >            
  <ContextProvider>
  <TokenRefresher/>    
    <Outlet />     
   </ContextProvider>
   </ErrorBoundary>
//  </Suspense>
);

const AppRouter = () => {

    

  
  const router = createBrowserRouter([
    {
      element: <ErrorBoundaryLayout />,
      errorElement : <ErrorPage/>,
         
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
        { path: 'error_server', element: <Maintenance/> },

        {
          path: 'chat',
          element: <ProtectedRoute><ChatPage/></ProtectedRoute>,
          children: [
             { path : 'chatrooms', element: <ChatRoomList/> },
          ]
        },

        {
          path: '/profile',
          element: <ProtectedRoute><Profile /></ProtectedRoute>,
          loader: loaderProfile,   
          
        },     
        {
          path: 'profile/address',
          element: <ProtectedRoute><Address /></ProtectedRoute>,
          loader: loaderAddress,
        },
        {
          path: 'profile/orders',
          element: <ProtectedRoute><Orders /></ProtectedRoute>,
          loader : loaderOrders
        },
        {
          path: 'profile/payment',
          element: <ProtectedRoute><Payment /></ProtectedRoute>,
          loader: loaderPayment
        },
        {
          path: 'profile/security',
          element: <ProtectedRoute><Security /></ProtectedRoute>,
          loader : loaderSecurity
        },
        {
          path: '/product/:slug',          
          element: <SingleProduct />,
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
        },
        {
          path: '/order/:id',
          element: <ProtectedRoute><OrderPage /></ProtectedRoute>,
          loader: loaderOrder,
        },
        // {
        //   path: '/admin',
        //   element: <DashboardLayout />,
          
        //   children: [
        //     { path: 'category', element: <Categories /> },                        
        //     { path: 'product', element: <AdminProduct />},
        //     { path: 'allproducts', 
        //       element: <Products />, 
        //       loader:loaderProducts
        //     }
        //   ]
        // }
      ]
    }]);
  

  return <RouterProvider router={router}/>     

};



function App() {

 
  
  
  // const loadData = async () => {

  //   await loadFakeData();            
  // }

  //useOnMounted(loadData);
  
  
  return (
    <>   
    <div>
    <Toaster/>
    </div>
      <QueryClientProvider client={queryClient}>      
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
          <PayPalScriptProvider options={initialOptions} >                      
            <AppRouter/>                 
             </PayPalScriptProvider>
          </PersistGate>
        </Provider>
      
      </QueryClientProvider>
    </>
  );
}


export default App;
