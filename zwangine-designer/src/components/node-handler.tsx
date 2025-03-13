import {Handle, Position, useNodeConnections} from "@xyflow/react";
import {HandleType} from "@xyflow/system";
import {NodeHandleCountType} from "@/@enum/Node-handle-count-type.ts";

export function NodeHandler(props:{connectionCount:NodeHandleCountType , type:HandleType, position:Position }){
    const connections = useNodeConnections({
        handleType: props.type,
    });

    return (
        <Handle
            type={props.type}
            position={props.position}
            isConnectable={connections.length < props.connectionCount}
        />
    );
}