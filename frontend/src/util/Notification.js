import { useLogout } from "hook/hooks";

const {
  EventSourcePolyfill,
  NativeEventSource,
} = require("event-source-polyfill");
const { useEffect, useState, useRef } = require("react");
const { useAuthContext } = require("store/AuthContext");

export const useNotification = () => {
  const { token, isAuthenticated } = useAuthContext();
  const { logout, isPending } = useLogout(true);

  const [isConnected, setIsConnected] = useState(false);

  const EventSoruce = EventSourcePolyfill || NativeEventSource;
  const eventSource = useRef(null);
  const reconnectAttempts = useRef(0);
  const timerId = useRef(null);

  console.log(token);

  const fetchSSE = () => {
    // 최대 재연결 횟수 제한
    if (reconnectAttempts.current > 5) {
      console.log("Max reconnection attempts reached.");
      return;
    }
    if (!isAuthenticated || isPending) return;

    eventSource.current = new EventSoruce(
      `${process.env.REACT_APP_API_URL}/user-service/api/sse`,
      {
        headers: {
          "Content-Type": "text/event-stream",
          "Cache-Control": "no-cache",
          //'Connection' : 'keep-alive', // http/2에서는 무시됨
          "X-Accel-Buffering": "no",
          Authorization: `Bearer ${token}`,
        },
        heartbeatTimeout: 120000,
        withCredentials: true,
      }
    );

    eventSource.current.onopen = () => {
      console.log("SSE connection opened.");
      setIsConnected(true);
      reconnectAttempts.current = 0; // 성공 시 재시도 횟수 초기화
      clearTimeout(timerId.current); // 성공 시 타이머 취소
    };

    eventSource.current.addEventListener("notification", (event) => {
      //const data = JSON.parse(event.data)
      console.log("[SSE] message ", event.data);

      if (event.data === "Logout") {
        logout();
      }
    });

    eventSource.current.onmessage = (event) => {
      console.log(event.data);
    };

    eventSource.current.onerror = () => {
      console.log("SSE connection error occurred.");
      eventSource.current?.close();
      setIsConnected(false);
      
      // 지수 백오프 로직
      const delay = Math.min(1000 * (2 ** reconnectAttempts.current), 30000); // 1초, 2초, 4초, ... 최대 30초
      console.log(`Attempting to reconnect in ${delay / 1000} seconds.`);
      reconnectAttempts.current += 1;

      timerId.current = setTimeout(fetchSSE, delay);
    };
  };

  useEffect(() => {
    fetchSSE();
    return () => {
      eventSource.current?.close();
       clearTimeout(timerId.current);
    };
  }, []);  // [] 의존성 배열로 최초 1회만 실행
};

export default function Notification() {
  useNotification();
  return <></>;
}
