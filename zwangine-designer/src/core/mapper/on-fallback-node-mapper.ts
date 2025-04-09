import {ProcessorDefinition} from "@/@types";
import {NodeIconResolver, NodeIconType} from "@/lib/utility";
import {IElementLookupResult, WorkflowVisualEntityData} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";
import {BaseNodeMapper} from "./base-node-mapper.ts";
import {createVisualizationNode} from "../visualization-node.ts";

export class OnFallbackNodeMapper extends BaseNodeMapper {
    getVizNodeFromProcessor(
        path: string,
        _componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        const processorName: keyof ProcessorDefinition = 'onFallback';

        const data: WorkflowVisualEntityData = {
            path,
            icon: NodeIconResolver.getIcon(processorName, NodeIconType.EIP),
            processorName,
            isGroup: true,
        };

        const vizNode = createVisualizationNode(path, data);

        const children = this.getChildrenFromBranch(`${path}.steps`, entityDefinition);
        children.forEach((child) => {
            vizNode.addChild(child);
        });

        return vizNode;
    }
}
