import { shallow } from 'zustand/shallow';
import { createWithEqualityFn } from 'zustand/traditional';
import {SchemaDefinition} from "@/core";

interface SchemasState {
    schemas: { [key: string]: UaSchemaDefinition };
    setSchema: (schemaKey: string, schema: UaSchemaDefinition) => void;
}

export const useSchemasStore = createWithEqualityFn<SchemasState>(
    (set) => ({
        schemas: {},
        setSchema: (schemaKey: string, schema: UaSchemaDefinition) => {
            set((state) => ({
                schemas: {
                    ...state.schemas,
                    [schemaKey]: schema,
                },
            }));
        },
    }),
    shallow,
);
