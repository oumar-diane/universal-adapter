import {SchemaDefinition} from "../schema/schema.ts";
import {PropertyCommon} from "../properties-common.ts";
import {CatalogKind} from "./catalog-kind.ts";

export interface IProcessorDefinition {
    model: IProcessorModel;
    properties: Record<string, IProcessorProperty>;
    propertiesSchema?: SchemaDefinition['schema'];
}

export interface IProcessorModel {
    kind: CatalogKind.Processor;
    name: string;
    title: string;
    description?: string;
    deprecated: boolean;
    label: string;
    javaType?: string;
    abstract?: boolean;
    input?: boolean;
    output?: boolean;
}

export interface IProcessorProperty extends PropertyCommon {
    oneOf?: string[];
    type: string;
}
