import {SerializerType} from "./model/resource/resource-serializer.ts";
import {SourceSchemaType} from "./model/schema/schema.ts";
import {Resource} from "./model/resource/resource.ts";

export function isXML(code: unknown): boolean {
    if (typeof code !== 'string') {
        return false;
    }
    const trimmedCode = code.trim();
    return trimmedCode.startsWith('<') && trimmedCode.endsWith('>');
}

const DSL_LISTS: Record<SerializerType, SourceSchemaType[]> = {
    [SerializerType.YAML]: [SourceSchemaType.Workflow],
};

export function getSupportedDsls(camelResource: Resource) {
    return DSL_LISTS[camelResource.getSerializerType()];
}