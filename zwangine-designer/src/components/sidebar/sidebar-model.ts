import {ComponentClass} from "react";
import {SVGIconProps} from "@patternfly/react-icons/dist/js/createIcon";

export interface SidebarItemData{
    id:string
    title: string;
    route: string;
    icon: ComponentClass<SVGIconProps, any>
}
