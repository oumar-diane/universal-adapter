import {DATAMAPPER_ID_PREFIX, isDataMapperNode, NodeIconResolver, NodeIconType} from '@/lib/utility';
import {Step} from "@/@types";
import {IElementLookupResult, WorkflowVisualEntityData} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";
import {BaseNodeMapper} from "./base-node-mapper.ts";
import {createVisualizationNode} from "../visualization-node.ts";

export class DataMapperNodeMapper extends BaseNodeMapper {
    getVizNodeFromProcessor(
        path: string,
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        _componentLookup: IElementLookupResult,
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        _entityDefinition: unknown,
    ): IVisualizationNode {
        const processorName = DATAMAPPER_ID_PREFIX;

        const data: WorkflowVisualEntityData = {
            path,
            icon: NodeIconResolver.getIcon(processorName, NodeIconType.EIP),
            processorName: DATAMAPPER_ID_PREFIX,
            isGroup: false,
        };

        return createVisualizationNode(processorName, data);
    }

    static isDataMapperNode(stepDefinition: Step): boolean {
        return isDataMapperNode(stepDefinition);
    }
}
