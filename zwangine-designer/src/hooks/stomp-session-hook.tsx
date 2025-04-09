import {useContext} from "react";
import {StompSessionContext} from "@/providers/stomp-session-provider.tsx";

export const useStompSession = () => {
    const ctx = useContext(StompSessionContext);

    if (!ctx) throw new Error('StompSessionContext needs to be called inside `StompSessionProvider`');

    return ctx;
};
