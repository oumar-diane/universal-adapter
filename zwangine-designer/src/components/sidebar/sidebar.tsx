import {FunctionComponent, useEffect} from "react";
import {Stack, StackItem} from "@patternfly/react-core";
import {SidebarItem, SidebarItemData} from "@/components/sidebar";
import {NodeProps} from "postcss";
import {useSidebarItemContext} from "@/hooks/use-sidebar-item-context.tsx";
import {useLocation} from "react-router";


export const Sidebar: FunctionComponent<NodeProps> = () => {
    const sidebarItemState = useSidebarItemContext()
    const location = useLocation()

    useEffect(() => {
        const path = location.pathname
        switch (path){
            case "/":
                sidebarItemState.setActiveItemHandler(sidebarItemState?.sidebarItems["canvas"])
                break;
            case "/catalog":
                sidebarItemState.setActiveItemHandler(sidebarItemState?.sidebarItems["catalog"])
                break;
            default:
                sidebarItemState.setActiveItemHandler(sidebarItemState.sidebarItems["canvas"])
        }
    })
    return (
        <div className="flex flex-col bg-primary gap-2 h-full">
            <Stack>
                {Object.values(sidebarItemState.sidebarItems).map((item:SidebarItemData) => (
                    <StackItem key={"stack-item-" + item.id}>
                        <SidebarItem key={"sidebar-item" + item.id} id={item.id} route={item.route} title={item.title} icon={item.icon}/>
                    </StackItem>
                ))}
            </Stack>
        </div>
    );
};


