import { createContext, FunctionComponent, PropsWithChildren, useContext, useMemo } from 'react';
import {SchemaDefinition} from "@/core";
import {SchemaDefinitionsContext} from "@/components/visualization";
import {resolveSchemaWithRef} from "@/lib/utility";

export interface SchemaContextValue {
    schema: SchemaDefinition['schema'];
    definitions: Record<string, SchemaDefinition['schema']>;
}

export const SchemaContext = createContext<SchemaContextValue>({ schema: {}, definitions: {} });

export const SchemaProvider: FunctionComponent<PropsWithChildren<{ schema: SchemaDefinition['schema'] }>> = ({
                                                                                                                      schema,
                                                                                                                      children,
                                                                                                                  }) => {
    const { definitions, omitFields } = useContext(SchemaDefinitionsContext);

    const value = useMemo(() => {
        const resolvedSchema = resolveSchemaWithRef(schema, definitions);

        if (Array.isArray(resolvedSchema.anyOf)) {
            resolvedSchema.anyOf = resolvedSchema.anyOf.map((anyOfSchema) => resolveSchemaWithRef(anyOfSchema, definitions));
        }
        if (Array.isArray(resolvedSchema.oneOf)) {
            resolvedSchema.oneOf = resolvedSchema.oneOf.map((oneOfSchema) => resolveSchemaWithRef(oneOfSchema, definitions));
        }

        omitFields.forEach((field) => {
            delete resolvedSchema.properties?.[field];
        });

        return { schema: resolvedSchema, definitions };
    }, [definitions, omitFields, schema]);

    return <SchemaContext.Provider value={value}>{children}</SchemaContext.Provider>;
};
