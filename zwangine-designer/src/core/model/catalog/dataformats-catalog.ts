import {SchemaDefinition} from "../schema/schema.ts";
import {IProcessorModel, IProcessorProperty} from "./processors-catalog.ts";


export interface IDataformatDefinition {
    model: IDataformatModel;
    properties: Record<string, IDataformatProperty>;
    propertiesSchema: SchemaDefinition['schema'];
}

export type IDataformatModel = IProcessorModel;

export type IDataformatProperty = IProcessorProperty;
