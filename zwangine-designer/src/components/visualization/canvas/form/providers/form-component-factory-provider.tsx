import { SchemaDefinition } from '@/core';
import { createContext, FunctionComponent, PropsWithChildren, useCallback } from 'react';
import {
    AllOfField, ArrayField, BooleanField,
    DisabledField, EnumField, ExpressionField, ObjectField, OneOfField, PasswordField, StringField, TextAreaField, FieldProps
} from "@/components/visualization";
import {IPropertiesField, PropertiesField} from '@/components/form';
import {ConnectedField} from "uniforms";
type FormComponentFactoryContextValue = (schema: SchemaDefinition['schema']) => FunctionComponent<FieldProps> | ConnectedField<IPropertiesField, any>;

/* Name of the properties that should load TextAreaField */
const TextAreaPropertyNames = ['Expression', 'Description', 'Query', 'Script'];

// eslint-disable-next-line react-refresh/only-export-components
export const FormComponentFactoryContext = createContext<FormComponentFactoryContextValue | undefined>(undefined);

export const FormComponentFactoryProvider: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const factory = useCallback<FormComponentFactoryContextValue>((schema) => {
        if (schema.format === 'password') {
            return PasswordField;
        } else if (schema.type === 'string' && schema.title && TextAreaPropertyNames.includes(schema.title)) {
            return TextAreaField;
        } else if (schema.type === 'string' && Array.isArray(schema.enum)) {
            return EnumField;
        } else if (schema.type === 'object' && Object.keys(schema?.properties ?? {}).length === 0) {
            /*
             * If the object has no properties, we render a generic key-value pairs field
             * This is useful for langchain4j-tools consumer components or when configuring beans entities
             */
            return PropertiesField;
        } else if (schema.format === 'expression' || schema.format === 'expressionProperty') {
            return ExpressionField;
        }

        switch (schema.type) {
            case 'string':
            case 'number':
            case 'integer':
                return StringField;
            case 'boolean':
                return BooleanField;
            case 'object':
                return ObjectField;
            case 'array':
                return ArrayField;
        }

        if (Array.isArray(schema.oneOf)) {
            return OneOfField;
        } else if (Array.isArray(schema.allOf)) {
            return AllOfField;
        } else if (Array.isArray(schema.anyOf)) {
            throw new Error('FormComponentFactoryProvider: AnyOf should be handled in the scope of the ObjectField');
        }

        return DisabledField;
    }, []);

    return <FormComponentFactoryContext.Provider value={factory}>{children}</FormComponentFactoryContext.Provider>;
};
