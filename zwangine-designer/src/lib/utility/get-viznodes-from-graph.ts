import type { Graph, GraphElement } from '@patternfly/react-topology';
import { isDefined } from './is-defined.ts';
import {IVisualizationNode} from "@/core";
import { CanvasNode } from '@/components/visualization';

const getVisualizationNodeFromCanvasNode = (
    node: GraphElement<CanvasNode, CanvasNode['data']>,
    predicate: (vizNode: IVisualizationNode) => boolean,
    accumulator: IVisualizationNode[],
): IVisualizationNode[] => {
    const vizNode = node.getData()?.vizNode;
    if (isDefined(vizNode) && predicate(vizNode)) {
        accumulator.push(vizNode);
    }

    node.getChildren().forEach((child) => {
        getVisualizationNodeFromCanvasNode(child, predicate, accumulator);
    });

    return accumulator;
};

export const getVisualizationNodesFromGraph = (
    graph: Graph,
    predicate: (vizNode: IVisualizationNode) => boolean = () => true,
): IVisualizationNode[] => {
    const vizNodes: IVisualizationNode[] = [];

    graph.getNodes().forEach((node) => {
        getVisualizationNodeFromCanvasNode(node, predicate, vizNodes);
    });

    return vizNodes;
};
