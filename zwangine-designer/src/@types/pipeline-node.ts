
export type PipelineNode = {
    id: string,
    type: string,
    position: NodePosition,
    data: {[index:string]:any}
}

export type NodePosition = {
    x: number,
    y: number
}

export type PipelineEdge = {
    id: string,
    source: string,
    target: string,
}

export type PipelineState = {
    nodes: PipelineNode[]
    edges: PipelineEdge[]
}

export type PipelineNodeConfiguration = {[index:string]:any}
