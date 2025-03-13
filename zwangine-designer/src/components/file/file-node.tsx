import {Position} from '@xyflow/react';
import file from "@/assets/component/file.png"
import {Label} from "@radix-ui/react-label";


import {DropdownMenu, DropdownMenuContent, DropdownMenuTrigger,} from "@/components/ui/dropdown-menu"
import {FileNodeTab} from "@/components/file/file-node-tab.tsx";
import {PipelineNode} from "@/@types/pipeline-node.ts";
import {LuSave} from "react-icons/lu";
import {useDispatch, useSelector} from "react-redux";
import {PipelineAction, PipelineStore} from "@/lib/store.ts";
import {NodeHandler} from "@/components/node-handler.tsx";
import {NodeHandleCountType} from "@/@enum/Node-handle-count-type.ts";


export function FileNode(props :PipelineNode) {

    const pipelineState = useSelector((state:PipelineStore) => state.pipelinesNodeConfigs)
    const dispatch:PipelineAction = useDispatch()

    console.log("file node: ", props.data)
    function handleSave(event :MouseEvent) : void {
        switch (props.data.component["name"]) {
            case "read":
                console.log("file", props.data.component);
                break
            default:
                console.log("file", props.data.component);
        }
    }

    return (
        <div className="font-bold">
            <NodeHandler connectionCount={NodeHandleCountType.DEFAULT} type="source" position={Position.Left}/>
            <div className={`flex flex-col justify-center items-center border-2 border-primary  w-[100px] h-[100px] ${props.data.component["isSelecting"]? "outline-2 outline-offset-2 outline-solid outline-red-300":""}`}>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <img src={file} width={50} height={30} className="cursor-pointer"/>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent  className="flex flex-col gap-2 w-[500px] h-[500px] z-[500]">
                        <div className="flex flex-row justify-between">
                            <div className="flex flex-row space-x-1">
                                <img src={file} width={20} height={15} className="cursor-pointer"/>
                                <Label className="font-bold uppercase font-sans text-primary">{props.data["component"]["title"]}</Label>
                            </div>
                            <LuSave className="w-[30px] h-[20px] text-primary cursor-pointer" onClick={handleSave}/>
                        </div>

                        <FileNodeTab props={props} />
                    </DropdownMenuContent>
                </DropdownMenu>
                <Label>{props.data["component"]["name"]}</Label>
            </div>
            <NodeHandler  connectionCount={NodeHandleCountType.DEFAULT} type="target" position={Position.Right} />
        </div>
    )
}
