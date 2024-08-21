import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import RootPage from "./pages/Root";
import HomePage, {loader as productsLoader } from './pages/home/Home';
import RegisterPage from './pages/user/RegisterPage';
import SignInPage from './pages/user/SignInPage';
import DashboardLayout from './components/admin/DashboardLayout';
import { ContextProvider } from "./store/AuthContext";
import { persistor, store } from "./redux/store";
import Categories from './pages/admin/Category';
import SubCategories from './pages/admin/SubCategory';
import AdminProduct from './pages/admin/Product';
import SingleProduct, {loader as productLoader } from "./pages/Product";



const router = createBrowserRouter([
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
    path: '/admin',
    element: <DashboardLayout />,
    children: [
      { path: 'category', element: <Categories /> },
      { path: 'subcategory', element: <SubCategories /> },
      { path: 'product', element: <AdminProduct /> }
    ]
  }

]);


function App() {
  return (
    <>
    <ContextProvider>
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>          
            <RouterProvider router={router} />          
        </PersistGate>
      </Provider>
      </ContextProvider>
    </>
  );
}

export default App;
