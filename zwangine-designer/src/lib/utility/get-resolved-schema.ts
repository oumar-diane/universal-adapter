import { UaSchemaDefinition } from '@/core';
import { isDefined } from './is-defined.ts';
import { resolveRefIfNeeded } from './resolve-ref-if-needed.ts';

export const getResolvedSchema = (
    oneOf: UaSchemaDefinition['schema'],
    rootSchema?: UaSchemaDefinition['schema'],
) => {
    if (!(isDefined(oneOf?.properties) && isDefined(rootSchema))) return oneOf;

    const resolvedProperties = Object.keys(oneOf.properties).reduce(
        (acc, key) => {
            if (!(isDefined(oneOf.properties) && key in oneOf.properties)) return acc;

            const resolvedOneOfProperty = resolveRefIfNeeded(oneOf.properties[key], rootSchema);
            acc[key] = resolvedOneOfProperty;
            return acc;
        },
        {} as UaSchemaDefinition['schema'],
    );

    return Object.assign({}, oneOf, { properties: resolvedProperties });
};
