import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import RootPage  from "./pages/Root";
import HomePage from './pages/home/Home';
import RegisterPage from './pages/user/RegisterPage';
import SignInPage from './pages/user/SignInPage';

const router = createBrowserRouter([
{
  path: '/',
  element:<RootPage />,
  children : [    
    { index: true, element: <HomePage /> },
    { path : '/signin', element:<SignInPage/>},
    { path : '/register', element:<RegisterPage/>},

    { path : '/admin'      
    }
  ]
},

]);


function App() {
  return <RouterProvider router={router} />;  
}

export default App;
