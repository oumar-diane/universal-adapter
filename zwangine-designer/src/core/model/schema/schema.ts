import { JSONSchema4 } from 'json-schema';

export enum SourceSchemaType {
    Workflow = 'Workflow',
}

export interface SchemaDefinition {
    name: string;
    tags: string[];
    uri: string;
    schema: JSONSchema4;
}

export const enum NodeLabelType {
    Id = 'id',
    Description = 'description',
}
