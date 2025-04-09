import {ComponentClass, FunctionComponent} from "react";
import {SVGIconProps} from "@patternfly/react-icons/dist/js/createIcon";
import {RowSplitter, RowSplitterItem} from "@/components/sidebar/row-splitter.tsx";
import {useSidebarItemContext} from "@/hooks/use-sidebar-item-context.tsx";
import {useNavigate} from "react-router";

interface SidebarItemProps {
    id: string;
    title: string;
    icon: ComponentClass<SVGIconProps, any>;
    route: string;
}


export const SidebarItem: FunctionComponent<SidebarItemProps> = (props) => {

    const navigate = useNavigate()

    const sidebarItemState = useSidebarItemContext()

    function handleClick(event: React.MouseEvent<HTMLDivElement>) {
        event.stopPropagation();
        event.preventDefault();
        sidebarItemState.setActiveItemHandler(props)
        navigate(props.route)
    }
    return (
        <RowSplitter key={props.id} onClick={handleClick} isActive={sidebarItemState.activeItem?.id === props.id} >
            <RowSplitterItem>
                <props.icon className={"text-[#fff]"}/>
            </RowSplitterItem>
            <RowSplitterItem>
                <p className={"text-[#fff] font-bold "}>{props.title}</p>
            </RowSplitterItem>
        </RowSplitter>
    );
};


