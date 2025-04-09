import {createContext, FunctionComponent, PropsWithChildren, useMemo, useState} from "react";
import {NavItem} from "@/layouts/layout-model.ts";


interface MainHeaderActiveContextType{
    activeTab: NavItem;
    setActiveTabHandler: (tab: NavItem) => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const MainHeaderActiveContext = createContext<MainHeaderActiveContextType | null>(null);

export const MainHeaderActiveProvider:FunctionComponent<PropsWithChildren> = ({children}) => {
    const [activeTab, setActiveTab] = useState<NavItem>(NavItem.DESIGNER);

    function setActiveTabHandler(tab: NavItem){
        setActiveTab(tab);
    }

    const contextValue = useMemo(
        ()=> ({
            activeTab,
            setActiveTabHandler,
        }), [activeTab]);

    return (
        <MainHeaderActiveContext.Provider value={contextValue}>
            {children}
        </MainHeaderActiveContext.Provider>
    )
}