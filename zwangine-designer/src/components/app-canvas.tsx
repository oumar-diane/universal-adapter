import {
    ReactFlow,
    Controls,
    Background,
    NodeChange,
    Connection,
    addEdge,
    EdgeChange, applyEdgeChanges, useReactFlow
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import {GrDeploy} from "react-icons/gr";
import {useCallback} from "react";
import {FileNode} from "@/components/file/file-node.tsx";
import {useDispatch, useSelector} from "react-redux";
import {addNode, deleteNode, handleNodeSelection, setEdges, updateNode} from "@/lib/state/pipeline-store.ts";
import {PipelineAction, PipelineStore} from "@/lib/store.ts";
import {SchedulerNode} from "@/components/scheduler/scheduler-node.tsx";
import {PipelineNode} from "@/@types/pipeline-node.ts";
import {PgeventNode} from "@/components/pgevent/pgevent-node.tsx";
import {TimerNode} from "@/components/timer/timer-node.tsx";


const nodeTypes = {
    fileNode: FileNode,
    schedulerNode:SchedulerNode,
    pgeventNode:PgeventNode,
    timerNode:TimerNode,
};

export function AppCanvas() {

    const pipelineState = useSelector((state:PipelineStore) => state.pipeline)
    const dispatch:PipelineAction = useDispatch()
    const { getIntersectingNodes } = useReactFlow();


    const onNodesChange = useCallback(
        (changes: NodeChange[]) => {
            console.log("change", changes);
            // dispatch(setNodes(applyNodeChanges(changes, pipelineState.nodes)))
            changes.map((change)=>{
                if(change.type === "add"){
                    dispatch(addNode(change.item as PipelineNode));
                    console.log("add")
                }if(change.type === "remove"){
                    console.log("remove: ", change)
                    dispatch(deleteNode(change.id));
                }if(change.type === "position"){
                    dispatch(updateNode(change));
                    console.log("position")
                }if(change.type === "replace"){
                    console.log("replace: ", change)
                }if(change.type === "select"){
                    console.log("select: ", change)
                    dispatch(handleNodeSelection(change));
                }if(change.type === "dimensions"){
                    console.log("dimensions: ", change)
                }
            })
        },
        [dispatch, pipelineState]
    );

    const onNodeDrag = useCallback((_: MouseEvent, node: PipelineNode) => {
        const intersections = getIntersectingNodes(node).map((n) => n.id);
        console.log("intersections", intersections);
    }, []);

    const onEdgesChange = useCallback(
        (changes:EdgeChange[]) => {
            dispatch(setEdges(applyEdgeChanges(changes, pipelineState.edges)));
        }, [dispatch, pipelineState]
    );

    const onConnect = useCallback(
        (connection:Connection) => {
            dispatch(setEdges(addEdge(connection, pipelineState.edges)));
        }, [dispatch, pipelineState]
    );



    const onNodeDelete = useCallback(
        (deletedNodes:PipelineNode[]) => {
            console.log("deletedNodes", deletedNodes);
            deletedNodes.map((deletedNode:PipelineNode) => {
                dispatch(deleteNode(deletedNode.id));
            })
        }, [dispatch, pipelineState]
    );


    return (
        <div className="grid grid-rows-[40px_auto] ">
            <div className="flex flex-row  items-center justify-between w-full bg-primary px-1">
                <h4 className="text-white font-bold font-sans">Pipeline actions</h4>
                <div className="">
                    <GrDeploy width="40" className="w-[40px] text-white"/>
                </div>
            </div>
            <ReactFlow
                nodeTypes={nodeTypes}
                nodes={pipelineState.nodes}
                edges={pipelineState.edges}
                className='bg-primary'
                onNodeDrag={onNodeDrag}
                onNodesChange={onNodesChange}
                onEdgesChange={onEdgesChange}
                onConnect={onConnect}
                onNodesDelete={onNodeDelete}
            >
                <Background />
                <Controls />
            </ReactFlow>
        </div>
    );
}

