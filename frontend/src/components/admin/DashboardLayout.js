import Sidebar from "../Sidebar";
import { Outlet } from 'react-router-dom';

const DashboardLayout = () => {

    return (
      <div className="flex min-h-screen">
          //<Sidebar/>
          <div className="w-full md:pl-8 "><Outlet/></div>                    
        </div>
    );
}

export default DashboardLayout;