import { UaSchemaDefinition } from '@/core';
import { isDefined } from './is-defined.ts';
import { resolveRefIfNeeded } from './resolve-ref-if-needed.ts';

/**
 * Extracts a schema containing only the required properties.
 * Recursively resolves `$ref` if necessary.
 */
export function getRequiredPropertiesSchema(
    schema?: UaSchemaDefinition['schema'],
    resolveFromSchema?: UaSchemaDefinition['schema'],
): UaSchemaDefinition['schema'] {
    if (!isDefined(schema) || !isDefined(resolveFromSchema)) return {};

    const schemaProperties = schema.properties;
    const requiredProperties = schema.required as string[];

    if (!isDefined(schemaProperties)) {
        return { ...schema, properties: {} };
    }

    const requiredFormSchema = Object.entries(schemaProperties).reduce(
        (acc, [property, definition]) => {
            if ('$ref' in definition) {
                const objectDefinition = resolveRefIfNeeded(definition, resolveFromSchema);
                const subSchema = getRequiredPropertiesSchema(objectDefinition, resolveFromSchema);
                if (Object.keys(subSchema.properties as object).length > 0) {
                    acc[property] = subSchema;
                }
            } else if (definition['type'] === 'object' && 'properties' in definition) {
                const subSchema = getRequiredPropertiesSchema(definition, resolveFromSchema);
                if (Object.keys(subSchema.properties as object).length > 0) {
                    acc[property] = subSchema;
                }
            } else if (isDefined(requiredProperties) && requiredProperties.indexOf(property) > -1) {
                acc[property] = definition;
            }

            return acc;
        },
        {} as UaSchemaDefinition['schema'],
    );

    return { ...schema, properties: requiredFormSchema };
}
