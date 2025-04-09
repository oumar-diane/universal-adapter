import { isDefined } from './is-defined.ts';
import { resolveSchemaWithRef } from './resolve-ref-if-needed.ts';
import {UaSchemaDefinition} from "@/core";

const PRIMITIVE_TYPES = ['string', 'number', 'boolean'];

/**
 * Weights a JSON schema against model definition.
 *
 * The goal of this function is to determine how well a schema fits a model.
 * @param model The model to weight against.
 * @param schema The schema to weight.
 * @param definitions The definitions to resolve against.
 * @returns The weight of the schema against the model.
 */
export const weightSchemaAgainstModel = (
    model: unknown,
    schema: UaSchemaDefinition['schema'],
    definitions: Record<string, UaSchemaDefinition['schema']>,
): number => {
    if (!isDefined(model) || !isDefined(schema)) return 0;

    const resolvedSchema = resolveSchemaWithRef(schema, definitions);

    const modelType = typeof model;
    if (PRIMITIVE_TYPES.includes(modelType)) {
        if (resolvedSchema.type === modelType) {
            return 10;
        }

        return 0;
    }

    if (typeof model === 'object' && Object.keys(model).length === 0 && resolvedSchema.type === 'object') {
        return 10;
    }

    return Object.entries(model).reduce((points, [modelKey, modelValue]) => {
        if (isDefined(resolvedSchema.properties)) {
            if (resolvedSchema.properties[modelKey]) {
                points += 1;
            }

            const resolvedSchemaProperty = resolveSchemaWithRef(resolvedSchema.properties[modelKey], definitions);
            if (isDefined(resolvedSchemaProperty) && typeof modelValue === resolvedSchemaProperty.type) {
                points += 10;
            }

            if (isDefined(resolvedSchemaProperty) && typeof modelValue === 'object') {
                points += weightSchemaAgainstModel(modelValue, resolvedSchemaProperty, definitions);
            }
        }

        resolvedSchema.anyOf?.forEach((anyOfSchema: any) => {
            const anyOfResolvedSchema = resolveSchemaWithRef(anyOfSchema, definitions);
            points += weightSchemaAgainstModel(model, anyOfResolvedSchema, definitions);
        });

        resolvedSchema.oneOf?.forEach((oneOfSchema: any) => {
            const oneOfResolvedSchema = resolveSchemaWithRef(oneOfSchema, definitions);
            points += weightSchemaAgainstModel(model, oneOfResolvedSchema, definitions);
        });

        return points;
    }, 0);
};
