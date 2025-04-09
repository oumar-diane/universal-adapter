import { FunctionComponent, useContext } from 'react';
import {SchemaContext, SchemaProvider, AutoField, FieldProps} from "@/components/visualization";
import {isDefined} from "@/lib/utility";

interface ObjectFieldInnerProps extends FieldProps {
    requiredProperties: string[];
}

export const ObjectFieldInner: FunctionComponent<ObjectFieldInnerProps> = ({ propName, requiredProperties }) => {
    const { schema } = useContext(SchemaContext);

    return (
        <>
            {Object.entries(schema.properties ?? {})
                .filter(([_, propertySchema]) => {
                    /** Remove empty properties like `csimple: {}` */
                    return isDefined(propertySchema) && Object.keys(propertySchema).length > 0;
                })
                .map(([propertyName, propertySchema]) => {
                    const name = `${propName}.${propertyName}`;
                    const required = requiredProperties.includes(propertyName);

                    return (
                        <SchemaProvider key={name} schema={propertySchema}>
                            <AutoField propName={name} required={required} />
                        </SchemaProvider>
                    );
                })}
        </>
    );
};
