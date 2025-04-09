import {ProcessorDefinition, Step} from "@/@types";
import {DATAMAPPER_ID_PREFIX, getValue, NodeIconResolver, NodeIconType} from "@/lib/utility";
import {IElementLookupResult, WorkflowVisualEntityData} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";
import { BaseNodeMapper } from "./base-node-mapper.ts";
import {createVisualizationNode} from "../visualization-node.ts";
import {DataMapperNodeMapper} from "./datamapper-node-mapper.ts";

export class StepNodeMapper extends BaseNodeMapper {
    getVizNodeFromProcessor(
        path: string,
        _componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        const processorName: keyof ProcessorDefinition = 'step';

        const data: WorkflowVisualEntityData = {
            path,
            icon: NodeIconResolver.getIcon(processorName, NodeIconType.EIP),
            processorName,
            isGroup: true,
        };

        const stepDefinition: Step = getValue(entityDefinition, path);
        if (DataMapperNodeMapper.isDataMapperNode(stepDefinition)) {
            return this.rootNodeMapper.getVizNodeFromProcessor(
                path,
                {
                    processorName: DATAMAPPER_ID_PREFIX,
                },
                entityDefinition,
            );
        }

        const vizNode = createVisualizationNode(processorName, data);

        const children = this.getChildrenFromBranch(`${path}.steps`, entityDefinition);
        children.forEach((child) => {
            vizNode.addChild(child);
        });

        return vizNode;
    }
}
