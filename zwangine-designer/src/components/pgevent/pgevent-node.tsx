import {Handle, NodeProps, Position} from '@xyflow/react';
import pgsql from "@/assets/component/pgsql.png"
import {Label} from "@radix-ui/react-label";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {FileNodeTab} from "@/components/file/file-node-tab.tsx";
import {NodeHandler} from "@/components/node-handler.tsx";
import {NodeHandleCountType} from "@/@enum/Node-handle-count-type.ts";



export function PgeventNode(props :NodeProps) {

    return (
        <div className="font-bold">
            <NodeHandler  connectionCount={NodeHandleCountType.DEFAULT} type="source" position={Position.Left}/>
            <div className={`flex flex-col justify-center items-center border-2 border-primary  w-[100px] h-[100px] ${props.data.component["isSelecting"]? "outline-2 outline-offset-2 outline-solid outline-red-300":""}`}>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <img src={pgsql} width={50} height={30} className="cursor-pointer"/>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent className=" flex flex-col gap-2 w-[500px] h-[500px] z-50">
                        <div className="flex flex-row space-x-1">
                            <img src={pgsql} width={20} height={15} className="cursor-pointer"/>
                            <Label className="font-bold uppercase font-sans text-primary">{props.data.component["title"]}</Label>
                        </div>
                        <FileNodeTab props={props} />
                    </DropdownMenuContent>
                </DropdownMenu>
                <Label>{props.data.component["name"]}</Label>
            </div>
            <NodeHandler  connectionCount={NodeHandleCountType.DEFAULT} type="target" position={Position.Right} />
        </div>
    )
}
