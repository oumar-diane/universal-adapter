
import {ProcessorDefinition} from "@/@types";
import {NodeIconResolver, NodeIconType} from "@/lib/utility";
import { BaseNodeMapper } from "./base-node-mapper.ts";
import {createVisualizationNode} from "../visualization-node.ts";
import {IElementLookupResult, WorkflowVisualEntityData} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";

export class ChoiceNodeMapper extends BaseNodeMapper {
    getVizNodeFromProcessor(
        path: string,
        _componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        const processorName: keyof ProcessorDefinition = 'choice';

        const data: WorkflowVisualEntityData = {
            path,
            icon: NodeIconResolver.getIcon(processorName, NodeIconType.EIP),
            processorName,
            isGroup: true,
        };

        const vizNode = createVisualizationNode(path, data);

        const whenNodes = this.getChildrenFromArrayClause(`${path}.when`, entityDefinition);
        whenNodes.forEach((whenNode) => {
            vizNode.addChild(whenNode);
        });

        const otherwiseNode = this.getChildrenFromSingleClause(`${path}.otherwise`, entityDefinition);
        if (otherwiseNode.length > 0) {
            vizNode.addChild(otherwiseNode[0]);
        }

        return vizNode;
    }
}
