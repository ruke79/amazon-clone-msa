import { Outlet } from 'react-router-dom';
import Header from './header/Header';
import Footer from './Footer';
import MenuSideBar from './header/MenuSidebar';

function RootPage() {

    return (
        <>      
      <Header />      
      <main>
        {/* {navigation.state === 'loading' && <p>Loading...</p>} */}
        <Outlet />
      </main>
      <Footer />
      <MenuSideBar />
    </>
  );
}

export default RootPage;


