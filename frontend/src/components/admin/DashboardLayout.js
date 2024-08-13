import Sidebar from "../Sidebar";
import { Outlet } from 'react-router-dom';

const DashboardLayout = () => {

    return (
        <>
          <Sidebar/>
          <Outlet />        
        </>
    );
}