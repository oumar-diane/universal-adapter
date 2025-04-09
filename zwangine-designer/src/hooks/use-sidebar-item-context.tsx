import {useContext} from "react";
import {SidebarItemContext} from "@/providers/sidebar-item-provider.tsx";

export const useSidebarItemContext = () => {
    const ctx = useContext(SidebarItemContext);

    if (!ctx) throw new Error('useSidebarItemContext needs to be called inside `useSidebarItemProvider`');

    return ctx;
};
