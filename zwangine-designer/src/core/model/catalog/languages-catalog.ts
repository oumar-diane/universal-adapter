import {SchemaDefinition} from "../schema/schema.ts";
import {IProcessorModel, IProcessorProperty} from "./processors-catalog.ts";

export interface ILanguageDefinition {
    model: ILanguageModel;
    properties: Record<string, ILanguageProperty>;
    propertiesSchema: SchemaDefinition['schema'];
}

export type ILanguageModel = IProcessorModel;

export type ILanguageProperty = IProcessorProperty;
