import { createSlice } from '@reduxjs/toolkit'
import {PipelineNodeConfiguration} from "@/@types/pipeline-node.ts";
const initialState: {[index:string]:PipelineNodeConfiguration} = {}

export const pipelineConfigurationSlice = createSlice({
    name: 'pipelinesNodeConfigs',
    initialState: initialState,
    reducers: {
        patch: (state, action: {payload:PipelineNodeConfiguration}) => {
            state[action.payload["operation_id"] as string] = []
            state[action.payload["operation_id"] as string].push(action.payload)
            return state
        },
    },
})


// Action creators are generated for each case reducer function
export const { patch } = pipelineConfigurationSlice.actions

export default pipelineConfigurationSlice.reducer

