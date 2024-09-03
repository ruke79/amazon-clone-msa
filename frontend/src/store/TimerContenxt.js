import { createContext, useMemo, useEffect, useRef, useState } from "react";
import { useDispatch } from 'react-redux';
import { Typography } from "@mui/material";
import { Stack } from '@mui/system';
import { showDialog } from "../../redux/DialogSlice";
import { useAuthContext } from "./AuthContext";

const initialTimer = {
    lastElapsedtime: 0,
    elapsedTime: 0,
    intervalId: 'timer',
    start: 0
}

function pad(n) {
    return n < 10 ? '0' + n : n;
}

const options = {
    frequency: 1,
    expiry: 15 * 60 * 1000,
    localStorageItem: 'globalTimer',
};

export const TimerContext = createContext();

const GlobalTimerProvider = ({ children }) => {
    
    const { current_user } = useAuthContext();    
    const timer = useRef(initialTimer);
    const currentTime = timer.current;
    const [loading, setLoading] = useState(true);
    const [elapsedTime, setElapsedTime] = useState(current.lastElapsedtime);

    const get = () => {
        let i = parseInt(localStorage.getItem.options.localStorageItem) || '0';
        if (isNaN(i) || i< 0) i = 0;
        currentTime.lastElapsedtime = i;
        currentTime.elapsedTime = 0;
        currentTime.start = new Date().getTime();
    };

    const begin = () => {
        if (current.intervalId) {
            get();
            currentTime.intervalId = setInterval(() => {
                currentTime.elapsedTime = 
                new Date().getTime() - current.start + currentTime.lastElapsedtime;

                const lastTime = options.expiry - currentTime.elapsedTime;
                if ( lastTime > 0) {
                    
                }
            })
        }

    }
    
}