import { Provider } from "react-redux";
import { useMemo, Suspense  } from "react";
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
import ApiErrorHandler from 'error/ApiErrorHandler';
import { ErrorBoundary } from "react-error-boundary";
import { Outlet  } from 'react-router-dom';
import ErrorPage from "pages/Error";
import { QueryClientProvider } from '@tanstack/react-query'
import { queryClient } from 'util/api';
import {ReactErrorBoundaryComponent} from 'error/ApiErrorBoundary';
import Maintenance from 'components/error/Maintenance';


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
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

const ErrorBoundaryLayout = () => (
  
//<Suspense fallback={<div>Loading...</div>}>
  <ErrorBoundary FallbackComponent = {ErrorFallback} >    
  <ApiErrorHandler/>      
    <Outlet />  
  </ErrorBoundary>
//  </Suspense>
);

const AppRouter = () => {

  const authContext = useAuthContext();

  const router = useMemo(() => createBrowserRouter([
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
          path: '/profile',
          element: <Profile />,
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
          element: <Address />,
          loader: loaderAddress(authContext),
        },
        {
          path: 'profile/orders',
          element: <ProtectedRoute><Orders /></ProtectedRoute>,
        },
        {
          path: 'profile/payment',
          element: <Payment />,
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
  ]), []);

  return <RouterProvider router={router}/>  

};

function App() {


  return (
    <>
      <QueryClientProvider client={queryClient}>
      
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
          <ContextProvider>               
            <AppRouter/>            
            </ContextProvider>
          </PersistGate>
        </Provider>
      
      </QueryClientProvider>
    </>
  );
}


export default App;
