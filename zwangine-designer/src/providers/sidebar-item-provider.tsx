import {createContext, FunctionComponent, PropsWithChildren, useEffect, useMemo, useState} from "react";
import {SidebarItemData} from "@/components/sidebar";
import {CatalogIcon, DesktopIcon} from "@patternfly/react-icons";

interface SidebarItemState {
    sidebarItems: { [index: string]: SidebarItemData };
    activeItem: SidebarItemData | undefined;
    setActiveItemHandler: (item: SidebarItemData) => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const SidebarItemContext = createContext<SidebarItemState | undefined>(undefined);


export const SidebarItemProvider:FunctionComponent<PropsWithChildren> = (props) => {

    const [sidebarItems, setSidebarItems] = useState<{ [index: string]: SidebarItemData }>({});
    const [activeItem, setActiveItem] = useState<SidebarItemData|undefined>(undefined);

    function setActiveItemHandler(item:SidebarItemData) {
        setActiveItem(item);
    }

    useEffect(()=> {
        setSidebarItems({
            "canvas": {
                id: "designer",
                title: "Canvas",
                icon: DesktopIcon,
                route: "/"
            },
            "catalog": {
                id: "catalog",
                title: "Catalog",
                icon: CatalogIcon,
                route: "/catalog"
            }
        });
    }, [])
    const sidebarContext: SidebarItemState= useMemo(
        () => ({
            sidebarItems,
            activeItem,
            setActiveItemHandler
        }),
        [sidebarItems, activeItem],
    );

    return (
        <SidebarItemContext.Provider value={sidebarContext}>
            {props.children}
        </SidebarItemContext.Provider>
    )

}