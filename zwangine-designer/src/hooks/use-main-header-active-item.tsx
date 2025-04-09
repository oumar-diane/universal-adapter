import {useContext} from "react";
import {MainHeaderActiveContext} from "@/providers/main-header-active-provider.tsx";


export const useMainHeaderActiveItem = () => {
    const ctx = useContext(MainHeaderActiveContext);

    if(!ctx) {
        throw new Error('useMainHeaderActiveItem must be used within a MainHeaderActiveProvider');
    }
    return ctx;
}