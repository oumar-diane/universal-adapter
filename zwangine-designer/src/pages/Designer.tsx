import {SidebarHeader} from "@/components/ui/sidebar.tsx";
import {AppCanvas} from "@/components/app-canvas.tsx";
import {SearchForm} from "@/components/search-form.tsx";
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@radix-ui/react-collapsible";
import {ChevronRight} from "lucide-react";
import {Label} from "@radix-ui/react-label";
import {config} from "@/components/component-configuration.ts";
import { v4 as uuid } from 'uuid'
import {useDispatch, useSelector} from "react-redux";
import {PipelineAction , PipelineStore} from "@/lib/store.ts";
import {PipelineNode} from "@/@types/pipeline-node.ts";
import {addNode, getLastNode} from "@/lib/state/pipeline-store.ts";
import {ReactFlowProvider} from "@xyflow/react";


export function Designer(){

    const pipelineState = useSelector((state:PipelineStore) => state.pipeline)
    const dispatch:PipelineAction = useDispatch()

    function handleNodeClick(component:{[index:string]:any}){
        const lastNode = getLastNode(pipelineState.nodes)
        const _uuid = uuid()
        const newNode:PipelineNode = {
            id:_uuid,
            type:component["type"],
            data:{
                id:_uuid,
                component:component
            },
            position: lastNode? {x:lastNode.position.x + 120, y:lastNode.position.y}  : {x: 100, y: 100},
        }

        dispatch(addNode(newNode))
    }

    return (
        <div className="grid grid-cols-[15rem_auto] gap-1 w-full h-full">
            <div className="flex flex-col bg-primary gap-2 h-full">
                <SidebarHeader className="font-bold text-white">
                    Components
                </SidebarHeader>
                <SearchForm  />
               <div className="h-full flex flex-col gap-2">
                   {config.map((item, index) => (
                       <Collapsible key={index.toString()} defaultOpen className="group/collapsible text-white">
                           <CollapsibleTrigger className="flex flex-row px-1 justify-between w-full">
                               <Label className="font-bold">{item?.title}</Label>
                               <ChevronRight className="ml-auto transition-transform group-data-[state=open]/collapsible:rotate-90" />
                           </CollapsibleTrigger>
                           {item.operations.map((operation, index) => (
                               <CollapsibleContent key={index} className="pl-2 flex flex-col cursor-pointer" onClick={(_)=>handleNodeClick(operation)}>
                                   <div className="flex flex-row gap-2">
                                       <img src={item.logoPath} alt={"logo"}  width={20} height={10}/>
                                       <Label className="font-bold cursor-pointer">{operation?.title}</Label>
                                   </div>
                               </CollapsibleContent>
                           ))
                           }
                       </Collapsible>
                   ))}
               </div>
            </div>
            <ReactFlowProvider>
                <AppCanvas/>
            </ReactFlowProvider>
        </div>
    )
}