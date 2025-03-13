import { createSlice } from '@reduxjs/toolkit'
import {PipelineDocument} from "@/@types/pipeline-document.ts";
import {PipelineDocumentUtils} from "@/lib/pipeline/pipeline-document-builder.ts";
const initialState: PipelineDocument = {
    document:{}
}

export const pipelineDocumentSlice = createSlice({
    name: 'pipelinesDocument',
    initialState: initialState,
    reducers: {
        addFromUri: (state, action: {payload:{uri:string}}) => {
            state.document["from"] = PipelineDocumentUtils.newObject()
            state.document["from"]["uri"] = action.payload.uri
            state.document["from"]["steps"] = []
            return state
        }
    },
})

// Action creators are generated for each case reducer function
export const { addFromUri } = pipelineDocumentSlice.actions

export default pipelineDocumentSlice.reducer

