import { configureStore } from '@reduxjs/toolkit'
import pipelineSliceReducer from "@/lib/state/pipeline-store.ts";
import pipelineDocumentReducer from "@/lib/state/document-store.ts"
import pipelineConfigurationReducer from "@/lib/state/configuration-store.ts"

export const store =  configureStore({
    reducer: {
        pipeline: pipelineSliceReducer,
        pipelinesDocument:pipelineDocumentReducer,
        pipelinesNodeConfigs:pipelineConfigurationReducer
    },
})

export type PipelineStore = ReturnType<typeof store.getState>
export type PipelineAction = typeof store.dispatch