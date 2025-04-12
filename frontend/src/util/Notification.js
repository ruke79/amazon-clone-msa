import { useLogout } from "hook/hooks";

const { EventSourcePolyfill, NativeEventSource } = require("event-source-polyfill");
const { useEffect, useState, useRef } = require("react");
const { useAuthContext } = require("store/AuthContext");

export const useNotification = () => {

    const { token } = useAuthContext();
    const logout = useLogout();

    const [data, setData] = useState(false);
    
    const EventSoruce = EventSourcePolyfill || NativeEventSource;    
    const eventSource = useRef(null);
    
    console.log(token);

    useEffect(()=> {
        const fetchSSE = () => {

            if (!token)
                return;
            
            eventSource.current = new EventSoruce(
                `${process.env.REACT_APP_API_URL}/user-service/sse`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,                        
                    },
                    heartbeatTimeout: 60000,
                    withCredentials: true
                }
            );

            eventSource.current.addEventListener('notification', (event) => {
                //const data = JSON.parse(event.data)
                console.log("[SSE] message ", event.data);

                if(event.data === "Logout") {
                    logout();
                }

                setData(true);
            })

            eventSource.current.onmessage = (event) => {
                console.log(event.data);
            }
        
            eventSource.current.onerror = async() => {
                eventSource.current?.close();                
                setTimeout(fetchSSE, 3000);
            };
            eventSource.current.onopen = () => {

                console.log('SSE 연결');
            }

        };
        fetchSSE();
        return ()=> {
            eventSource.current?.close();
        }
    },[token]);
    
}

export default function Notification() {
    useNotification();
    return <></>;
}