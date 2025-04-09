import { UaSchemaDefinition } from '@/core';
import { OneOfSchemas } from './get-oneof-schema-list.ts';
import { weightSchemaProperties } from './weight-schema-properties.ts';
import { weightSchemaAgainstModel } from './weight-schemas-against-model.ts';

export const getAppliedSchemaIndex = (
    model: Record<string, unknown>,
    oneOfList: UaSchemaDefinition['schema'][],
    rootSchema: UaSchemaDefinition['schema'],
): number => {
    const schemaPoints = oneOfList
        .map((oneOf, index) => {
            const points = weightSchemaProperties(model, oneOf, rootSchema);
            return { index, points };
        })
        .filter((oneOf) => oneOf.points > 0)
        .sort((a, b) => b.points - a.points);

    return schemaPoints.length > 0 ? schemaPoints[0].index : -1;
};

export const getAppliedSchemaIndexV2 = (
    model: unknown,
    oneOfSchemaList: OneOfSchemas[],
    definitions: UaSchemaDefinition['schema']['definitions'] = {},
): number => {
    const schemaPoints = oneOfSchemaList
        .map(({ schema }, index) => {
            const points = weightSchemaAgainstModel(model, schema, definitions);
            return { index, points };
        })
        .filter((weigthedSchema) => weigthedSchema.points > 0)
        .sort((a, b) => b.points - a.points);

    return schemaPoints.length > 0 ? schemaPoints[0].index : -1;
};
