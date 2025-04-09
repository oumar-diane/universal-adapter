import { isDefined } from './is-defined.ts';
import {UaSchemaDefinition} from "@/core";

/**
 * Extracts the schema recursively containing only the filtered properties.
 */
export function getFilteredProperties(
    properties: UaSchemaDefinition['schema']['properties'],
    filter: string,
    omitFields?: string[],
): UaSchemaDefinition['schema']['properties'] {
    if (!isDefined(properties)) return {};

    const filteredFormSchema = Object.entries(properties).reduce(
        (acc, [property, definition]) => {
            if (!omitFields?.includes(property)) {
                if (definition['type'] === 'object' && 'properties' in definition) {
                    const subFilteredSchema = getFilteredProperties(definition['properties'], filter);
                    if (subFilteredSchema && Object.keys(subFilteredSchema).length > 0) {
                        acc![property] = { ...definition, properties: subFilteredSchema };
                    }
                } else if (property.toLowerCase().includes(filter)) {
                    acc![property] = definition;
                }
            }

            return acc;
        },
        {} as UaSchemaDefinition['schema']['properties'],
    );

    return filteredFormSchema;
}
