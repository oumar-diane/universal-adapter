import { UaSchemaDefinition } from '@/core';
import { resolveSchemaWithRef } from './resolve-ref-if-needed.ts';

export const getItemFromSchema = (
    schema: UaSchemaDefinition['schema'],
    definitions: Record<string, UaSchemaDefinition['schema']>,
) => {
    const resolvedSchema = resolveSchemaWithRef(schema, definitions);
    const defaultValue = resolvedSchema.default;
    const properties = resolvedSchema.properties ?? {};
    const required = Array.isArray(resolvedSchema.required) ? resolvedSchema.required : [];

    switch (resolvedSchema.type) {
        case 'string':
            return defaultValue ?? '';
        case 'boolean':
            return defaultValue ?? false;
        case 'number':
            return defaultValue ?? 0;
        case 'object':
            return Object.entries(properties).reduce(
                (acc, [key, value]) => {
                    if (required.includes(key)) {
                        // @ts-ignore
                        acc[key] = getItemFromSchema(value, definitions);
                    }

                    return acc;
                },
                {} as Record<string, unknown>,
            );
        default:
            return {};
    }
};
