import {Sidebar} from "@/components/sidebar";
import {Outlet} from "react-router";
import {SidebarItemProvider} from "@/providers/sidebar-item-provider.tsx";





export function DesignerPage(){

    return (
        <SidebarItemProvider>
            <div className="grid grid-cols-[15rem_auto] gap-1 w-full h-full">
                <Sidebar/>
                <div className="grid grid-rows-[auto] ">
                    <Outlet/>
                </div>
            </div>
        </SidebarItemProvider>

    )
}