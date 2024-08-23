import { Outlet } from 'react-router-dom';
import Header from './header/Header';

function RootPage() {

    return (
        <>
      <Header />
      <main>
        {/* {navigation.state === 'loading' && <p>Loading...</p>} */}
        <Outlet />
      </main>
    </>
  );
}

export default RootPage;


