import {SourceSchemaType} from "../model/schema/schema.ts";

export const getResourceTypeFromPath = (path?: string): SourceSchemaType | undefined => {
    if (path?.endsWith('.xml') || path?.endsWith('.yaml') || path?.endsWith('.yml')) {
        return SourceSchemaType.Workflow;
    }
    return SourceSchemaType.Workflow;
};
