import {ProcessorDefinition} from "@/@types";
import {IVisualizationNodeData} from "./entity/base-visual-entity.ts";

export interface IElementLookupResult {
    processorName: keyof ProcessorDefinition;
    componentName?: string;
}

export type WorkflowVisualEntityData = IVisualizationNodeData & IElementLookupResult;

/**
 * Interface to shape the properties from Processors that can be filled
 * with nested Processors.
 */
export interface ProcessorStepsProperties {
    /** Property name, f.i., `steps` */
    name: string;

    /**
     * Property handling type
     * single-clause: the property can have a single-clause type of processor, f.i. `otherwise` and `doFinally`
     * branch: the property have a list of `processors`, f.i. `steps`
     * array-clause: the property is an array of clause processors, usually in the shape of `expression`, f.i. `when` and `doCatch`
     */
    type: 'single-clause' | 'branch' | 'array-clause';
}
