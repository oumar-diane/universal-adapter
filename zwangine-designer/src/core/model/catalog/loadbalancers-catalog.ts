import {SchemaDefinition} from "../schema/schema.ts";
import {IProcessorModel, IProcessorProperty} from "./processors-catalog.ts";

export interface ILoadBalancerDefinition {
    model: ILoadBalancerModel;
    properties: Record<string, ILoadBalancerProperty>;
    propertiesSchema: SchemaDefinition['schema'];
}

export type ILoadBalancerModel = IProcessorModel;

export type ILoadBalancerProperty = IProcessorProperty;
