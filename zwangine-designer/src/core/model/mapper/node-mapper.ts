import {IElementLookupResult} from "../component-type.ts";
import {IVisualizationNode} from "../entity/base-visual-entity.ts";


export interface INodeMapper {
    getVizNodeFromProcessor(
        path: string,
        componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode;
}
