import { UaSchemaDefinition } from '@/core';
import { isDefined } from './is-defined.ts';
import { resolveRefIfNeeded } from './resolve-ref-if-needed.ts';

export function getUserUpdatedProperties(
    schemaProperties?: UaSchemaDefinition['schema']['properties'],
    inputModel?: Record<string, unknown>,
    resolveFromSchema?: UaSchemaDefinition['schema'],
): UaSchemaDefinition['schema']['properties'] {
    if (!isDefined(schemaProperties) || !isDefined(inputModel) || !isDefined(resolveFromSchema)) return {};

    const nonDefaultFormSchema = Object.entries(schemaProperties).reduce(
        (acc, [property, definition]) => {
            const inputValue = inputModel[property];
            if (!isDefined(inputValue)) return acc;

            if (
                definition['type'] === 'string' ||
                definition['type'] === 'boolean' ||
                definition['type'] === 'integer' ||
                definition['type'] === 'number'
            ) {
                if (definition['default'] != inputValue) {
                    acc![property] = definition;
                }
            } else if (
                definition['type'] === 'object' &&
                'properties' in definition &&
                Object.keys(inputValue as object).length > 0
            ) {
                const subSchema = getUserUpdatedProperties(
                    definition['properties'],
                    inputValue as Record<string, unknown>,
                    resolveFromSchema,
                );
                if (subSchema && Object.keys(subSchema).length > 0) {
                    acc![property] = { ...definition, properties: subSchema };
                }
            } else if ('$ref' in definition) {
                const objectDefinition = resolveRefIfNeeded(definition, resolveFromSchema);
                const subSchema = getUserUpdatedProperties(
                    objectDefinition['properties'] as UaSchemaDefinition['schema']['properties'],
                    inputValue as Record<string, unknown>,
                    resolveFromSchema,
                );
                if (subSchema && Object.keys(subSchema).length > 0) {
                    acc![property] = { ...objectDefinition, properties: subSchema };
                }
            } else if (definition['type'] === 'array' && (inputValue as unknown[]).length > 0) {
                acc![property] = definition;
            }

            return acc;
        },
        {} as UaSchemaDefinition['schema']['properties'],
    );

    return nonDefaultFormSchema!;
}
