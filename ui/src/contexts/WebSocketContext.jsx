import React, { createContext, useContext, useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import { useSettings } from "../contexts/SettingContext";

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
  const [client, setClient] = useState(null);
  const [logs, setLogs] = useState([]);
  const [executionState, setExecutionState] = useState("unknown");

  const { mainService } = useSettings();

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: `ws://${mainService}/ws`,
      reconnectDelay: 5000, 
      onConnect: () => {
        console.log("WebSocket connected!");

        stompClient.subscribe("/topic/logs", (message) => {
          setLogs((prevLogs) => [...prevLogs, JSON.parse(message.body)]);
        });

        stompClient.subscribe("/topic/status", (message) => {
          setExecutionState(message.body);
        });

        stompClient.publish({ destination: "/app/status" });

        setClient(stompClient);
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [mainService]);

  return (
    <WebSocketContext.Provider value={{ client, logs, executionState }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(WebSocketContext);
