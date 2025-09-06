import { useLogout } from "hook/hooks";

const { EventSourcePolyfill, NativeEventSource } = require("event-source-polyfill");
const { useEffect, useState, useRef } = require("react");
const { useAuthContext } = require("store/AuthContext");

export const useNotification = () => {

    const { token, isAuthenticated } = useAuthContext();
    const { logout, isPending } = useLogout(true);
    
    const [isConnected, setIsConnected] = useState(false);

    const EventSoruce = EventSourcePolyfill || NativeEventSource;    
    const eventSource = useRef(null);

    
    console.log(token);

    useEffect(()=> {
        const fetchSSE = () => {

            if (!token || !isAuthenticated)
                return;

            // if (eventSource.current) {
            //     console.log("기존 SSE 연결");
            //     return;
            // }
            if (!isConnected) {            
                eventSource.current = new EventSoruce(
                    `${process.env.REACT_APP_API_URL}/user-service/api/sse`,
                    {
                        headers: {
                            'Content-Type' : 'text/event-stream',
                            'Cache-Control': 'no-cache',
                            //'Connection' : 'keep-alive', // http/2에서는 무시됨
                            'X-Accel-Buffering' : 'no',                            
                            Authorization: `Bearer ${token}`,                        
                        },
                        heartbeatTimeout: 120000,
                        withCredentials: true
                    }
                );
            }

            eventSource.current.addEventListener('notification', (event) => {
                //const data = JSON.parse(event.data)
                console.log("[SSE] message ", event.data);

                if(event.data === "Logout") {
                    logout();
                }

                
            })

            eventSource.current.onmessage = (event) => {
                console.log(event.data);
            }
        
            eventSource.current.onerror = async() => {
                eventSource.current?.close();                
                setIsConnected(false);
                setTimeout(fetchSSE, 3000);

            };
            eventSource.current.onopen = () => {

                console.log('SSE 연결');
                setIsConnected(true);
            }

        };
        fetchSSE();
        return ()=> {
            eventSource.current?.close();
            setIsConnected(false);
        }
    },[token]);
    
}

export default function Notification() {
    useNotification();
    return <></>;
}