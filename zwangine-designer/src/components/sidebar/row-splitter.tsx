import {FunctionComponent, PropsWithChildren} from "react";

interface RowSplitter{
    isActive?: boolean;
    onClick?: (event:React.MouseEvent<HTMLDivElement>) => void;
}


export const RowSplitter: FunctionComponent<PropsWithChildren<RowSplitter>> = (props) => {
    return (
        <div className={`flex flex-row items-center cursor-pointer ${props.isActive? 'bg-[#2b2d42]':''}`} onClick={props.onClick}>
            {props.children}
        </div>
    );
};


export const RowSplitterItem: FunctionComponent<PropsWithChildren> = (props) => {
    return (
        <div className="px-1 py-2">
            {props.children}
        </div>
    );
};


