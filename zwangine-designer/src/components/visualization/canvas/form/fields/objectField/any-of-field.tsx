import { FunctionComponent } from 'react';
import { FieldProps } from '../../typing';
import { AutoField } from '../auto-field';
import {UaSchemaDefinition} from "@/core";
import {SchemaProvider} from "@/components/visualization";

interface AnyOfFieldProps extends FieldProps {
    anyOf: UaSchemaDefinition['schema']['anyOf'];
}

export const AnyOfField: FunctionComponent<AnyOfFieldProps> = ({ propName, anyOf }) => {
    return (
        <>
            {anyOf?.map((schema, index) => {
                return (
                    <SchemaProvider key={index} schema={schema}>
                        <AutoField propName={propName} />
                    </SchemaProvider>
                );
            })}
        </>
    );
};
