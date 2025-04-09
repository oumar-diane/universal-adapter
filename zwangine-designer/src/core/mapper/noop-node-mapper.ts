import {INodeMapper} from "../model/mapper/node-mapper.ts";
import {IElementLookupResult} from "../model/component-type.ts";
import {createVisualizationNode} from "../visualization-node.ts";

export const noopNodeMapper: INodeMapper = {
    getVizNodeFromProcessor: (path: string, componentLookup: IElementLookupResult, entityDefinition: unknown) => {
        return createVisualizationNode('noop', { path, componentLookup, entityDefinition });
    },
};
