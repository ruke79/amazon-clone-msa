import {
    ArrowRightIcon,
    ArrowLeftIcon,
    HomeIcon,
    CogIcon,
    UserIcon,
    EllipsisVerticalIcon,
  } from '@heroicons/react/24/outline';
  import { useState } from 'react';
  import ChatSidebarItem from './ChatSidebarItem';
import { useLogout } from 'hook/hooks';
  
  // This sidebar component is for both mobile and desktop
  function Sidebar({ children, expanded, setExpanded }) {
    return (
      <div className="relative">
        {/* 
          This div is used to create the background overlay when the sidebar is expanded
          It is only visible on mobile screens
        */}
        <div
          className={`fixed inset-0 -z-10 block bg-gray-400  ${expanded ? 'block sm:hidden' : 'hidden'}`}
        />
        <aside
          className={`box-border h-screen transition-all ${expanded ? 'w-5/6 sm:w-64' : 'w-0 sm:w-20'}`}
        >
          <nav className="flex h-full flex-col border-r bg-white shadow-sm">
            <div className="flex items-center justify-between p-4 pb-2">
              <img
                src={`${process.env.PUBLIC_URL}/assets/images/user-image-default.jpg`}
                className={`overflow-hidden transition-all ${
                  expanded ? 'w-32' : 'w-0'
                }`}
                alt=""
              />
              <div className={`${expanded ? '' : 'hidden sm:block'}`}>
                <button
                  onClick={() => setExpanded((curr)  => !curr)}
                  className="rounded-lg bg-gray-50 p-1.5 hover:bg-gray-100"
                >
                  {expanded ? (
                    <ArrowRightIcon className="h-6 w-6" />
                  ) : (
                    <ArrowLeftIcon className="h-6 w-6" />
                  )}
                </button>
              </div>
            </div>
            <ul className="flex-1 px-3">{children}</ul>
            <div className="flex border-t p-3">
              <img
                src="https://ui-avatars.com/api/?background=c7d2fe&color=3730a3&bold=true&name=Mark+Ruffalo"
                alt=""
                className="h-10 w-10 rounded-md"
              />
              <div
                className={`
                flex items-center justify-between
                overflow-hidden transition-all ${expanded ? 'ml-3 w-52' : 'w-0'}
            `}
              >                
                <EllipsisVerticalIcon className="h-6 w-6" />
              </div>
            </div>
          </nav>
        </aside>
      </div>
    );
  }


  
  export default function ChatDetail() {
    const [expanded, setExpanded] = useState(true);

    const logout = useLogout();

    const navBarItems = [
      {
        icon: <HomeIcon />,
        text: 'Home',
        link: '/',
        active: true,
      },
      {
        icon: <UserIcon />,
        subMenu: [      

          {
            icon: <CogIcon />,
            text: 'Chat Rooms',
            link: '/chat/chatrooms'
          },

          {
            icon: <CogIcon />,
            text: 'Log out',
            buttonCallback : logout,
          },
          
        ],
        text: 'Chat Menu',
      },
      {
        icon: <CogIcon />,
        text: 'Settings',
      },
    ];
  
    // Desktop Sidebar
    return (      
      <div className="flex-1 w-full h-full">
      <Sidebar expanded={expanded} setExpanded={setExpanded}>
        {navBarItems.map((item, index) => (
          <ChatSidebarItem key={index} expanded={expanded} {...item} />
        ))}
      </Sidebar>
      </div>
    );
  }