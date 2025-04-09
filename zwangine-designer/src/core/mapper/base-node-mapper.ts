import {getValue, NodeIconResolver, NodeIconType } from "@/lib/utility";
import {DoCatch, ProcessorDefinition, When1} from "@/@types";
import { INodeMapper } from "../model/mapper/node-mapper.ts";
import {IElementLookupResult, ProcessorStepsProperties, WorkflowVisualEntityData} from "../model/component-type.ts";
import { IVisualizationNode } from "../model/entity/base-visual-entity.ts";
import { ComponentSchemaService } from "../schema/component-schema-service.ts";
import {createVisualizationNode} from "../visualization-node.ts";

export class BaseNodeMapper implements INodeMapper {
    constructor(protected readonly rootNodeMapper: INodeMapper) {}
    getVizNodeFromProcessor(
        path: string,
        componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        const nodeIconType = componentLookup.componentName ? NodeIconType.Component : NodeIconType.EIP;
        const data: WorkflowVisualEntityData = {
            path,
            icon: NodeIconResolver.getIcon(ComponentSchemaService.getIconName(componentLookup), nodeIconType),
            processorName: componentLookup.processorName,
            componentName: componentLookup.componentName,
        };

        const vizNode = createVisualizationNode(path, data);

        const childrenStepsProperties = ComponentSchemaService.getProcessorStepsProperties(
            componentLookup.processorName,
        );

        if (childrenStepsProperties.length > 0) {
            vizNode.data.isGroup = true;
        }

        childrenStepsProperties.forEach((stepsProperty) => {
            const childrenVizNodes = this.getVizNodesFromChildren(path, stepsProperty, entityDefinition);

            childrenVizNodes.forEach((childVizNode) => {
                vizNode.addChild(childVizNode);
            });
        });

        return vizNode;
    }

    protected getVizNodesFromChildren(
        path: string,
        stepsProperty: ProcessorStepsProperties,
        entityDefinition: unknown,
    ): IVisualizationNode[] {
        const subpath = `${path}.${stepsProperty.name}`;

        switch (stepsProperty.type) {
            case 'branch':
                return this.getChildrenFromBranch(subpath, entityDefinition);

            case 'single-clause':
                return this.getChildrenFromSingleClause(subpath, entityDefinition);

            case 'array-clause':
                return this.getChildrenFromArrayClause(subpath, entityDefinition);

            default:
                return [];
        }
    }

    protected getChildrenFromBranch(path: string, entityDefinition: unknown): IVisualizationNode[] {
        const stepsList = getValue(entityDefinition, path, []) as ProcessorDefinition[];

        const branchVizNodes = stepsList.reduce((accStepsNodes, step, index) => {
            const singlePropertyName = Object.keys(step)[0];
            const childPath = `${path}.${index}.${singlePropertyName}`;
            const childComponentLookup = ComponentSchemaService.getComponentLookup(
                childPath,
                getValue(step, singlePropertyName),
            );

            const vizNode = this.rootNodeMapper.getVizNodeFromProcessor(childPath, childComponentLookup, entityDefinition);

            const previousVizNode = accStepsNodes[accStepsNodes.length - 1];
            if (previousVizNode !== undefined) {
                previousVizNode.setNextNode(vizNode);
                vizNode.setPreviousNode(previousVizNode);
            }

            accStepsNodes.push(vizNode);
            return accStepsNodes;
        }, [] as IVisualizationNode[]);

        /** Empty steps branch placeholder */
        if (branchVizNodes.length === 0) {
            const placeholderPath = `${path}.${branchVizNodes.length}.placeholder`;
            const previousNode = branchVizNodes[branchVizNodes.length - 1];
            const placeholderNode = createVisualizationNode(placeholderPath, {
                isPlaceholder: true,
                path: placeholderPath,
            });
            branchVizNodes.push(placeholderNode);

            if (previousNode) {
                previousNode.setNextNode(placeholderNode);
                placeholderNode.setPreviousNode(previousNode);
            }
        }

        return branchVizNodes;
    }

    protected getChildrenFromSingleClause(path: string, entityDefinition: unknown): IVisualizationNode[] {
        const childComponentLookup = ComponentSchemaService.getComponentLookup(path, entityDefinition);

        /** If the single-clause property is not defined, we don't create a IVisualizationNode for it */
        if (getValue(entityDefinition, path) === undefined) return [];

        return [this.rootNodeMapper.getVizNodeFromProcessor(path, childComponentLookup, entityDefinition)];
    }

    protected getChildrenFromArrayClause(path: string, entityDefinition: unknown): IVisualizationNode[] {
        const expressionList = getValue(entityDefinition, path, []) as When1[] | DoCatch[];

        return expressionList.map((_step, index) => {
            const childPath = `${path}.${index}`;
            const processorName = path.split('.').pop() as keyof ProcessorDefinition;
            const childComponentLookup = { processorName }; // when, doCatch

            return this.rootNodeMapper.getVizNodeFromProcessor(childPath, childComponentLookup, entityDefinition);
        });
    }
}
