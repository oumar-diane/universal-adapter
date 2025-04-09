import { SchemaDefinition } from '@/core';
import {getAppliedSchemaIndex, getValue, OneOfSchemas, ROOT_PATH} from '@/lib/utility';
import { useMemo } from 'react';
import { useForm } from 'uniforms';
import { useSchemaBridgeContext } from './schema-bridge-hook';

interface AppliedSchema {
    index: number;
    name: string;
    description?: string;
    schema: SchemaDefinition['schema'];
    model: Record<string, unknown>;
}

export const useAppliedSchema = (fieldName: string, oneOfSchemas: OneOfSchemas[]): AppliedSchema | undefined => {
    const form = useForm();
    const { schemaBridge } = useSchemaBridgeContext();

    const result = useMemo(() => {
        const currentModel = getValue(form.model, fieldName === '' ? ROOT_PATH : fieldName);

        const oneOfList = oneOfSchemas.map((oneOf) => oneOf.schema);
        const index = getAppliedSchemaIndex(
            currentModel,
            oneOfList,
            schemaBridge?.schema as SchemaDefinition['schema'],
        );
        if (index === -1) {
            return undefined;
        }

        const foundSchema = oneOfSchemas[index];

        return {
            index,
            name: foundSchema.name,
            description: foundSchema.description,
            schema: foundSchema.schema,
            model: currentModel,
        };
    }, [fieldName, form.model, oneOfSchemas, schemaBridge?.schema]);

    return result;
};
