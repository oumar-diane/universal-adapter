import { createSlice } from '@reduxjs/toolkit'
import {PipelineEdge, PipelineNode, PipelineState} from "@/@types/pipeline-node.ts";
const initialState: PipelineState = {
    edges:[],
    nodes:[]
}

export const pipelineSlice = createSlice({
    name: 'pipelines',
    initialState: initialState,
    reducers: {
        addNode: (state, action:{payload:PipelineNode}) => {
            state.nodes.push(action.payload)
            state.nodes.sort((a:PipelineNode, b:PipelineNode) => a.position.x - b.position.y);
        },
        setNodes: (state, action:{payload:PipelineNode[]}) => {
            state.nodes = action.payload.sort((a:PipelineNode, b:PipelineNode) => a.position.x - b.position.y);
            return state
        },
        setEdges: (state, action:{payload:PipelineEdge[]}) => {
            state.edges = action.payload
            return state
        },
        addEdge: (state, action:{payload:PipelineEdge}) => {
            state.edges.push(action.payload)
        },
        deleteNode: (state, action:{payload:string}) => {
            console.log("state changed: ", action.payload)
            const newState:PipelineNode[] = []
            state.nodes.map((_node:PipelineNode) => {
                if(action.payload !== _node.id){
                    newState.push(_node)
                }
            });
            state.nodes = newState
            state.nodes.sort((a:PipelineNode, b:PipelineNode) => a.position.x - b.position.y);
            return state;
        },
        handleNodeSelection(state, action: {payload:{[index:string]:any}}) {
            const node = state.nodes.find((n)=>n.id === action.payload["id"])
            const newState:PipelineNode[] = []
            state.nodes.map((_node:PipelineNode) => {
                if(action.payload["id"] !== _node.id){
                    newState.push(_node)
                }else{
                    node!.data.component["isSelecting"] = action.payload["selected"]
                    newState.push(node as PipelineNode)
                }
            });
            state.nodes = newState
            console.log("state: ", state.nodes)
        },
        updateNode: (state, action:{payload:{[index:string]:any}}) => {
            console.log("state changed: ", action.payload)
            const newState:PipelineNode[] = []
            state.nodes.map((_node:PipelineNode) => {
                if(action.payload["id"] !== _node.id){
                    newState.push(_node)
                }else{
                    _node.position = action.payload["position"]
                    newState.push(_node)
                }
            });
            state.nodes = newState
            state.nodes.sort((a:PipelineNode, b:PipelineNode) => a.position.x - b.position.y);
            return state;
        },


    },
})

export function getLastNode(state:PipelineNode[]):PipelineNode|null {
    return state.length >0? state[state.length - 1]:null
}

// Action creators are generated for each case reducer function
export const { addNode, setNodes, setEdges, addEdge, deleteNode, updateNode, handleNodeSelection } = pipelineSlice.actions

export default pipelineSlice.reducer