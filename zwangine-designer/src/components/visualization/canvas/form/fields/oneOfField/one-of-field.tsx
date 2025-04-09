import { FunctionComponent } from 'react';
import {SchemaList, SchemaProvider, useOneOfField, AutoField, FieldProps} from "@/components/visualization";

export const OneOfField: FunctionComponent<FieldProps> = ({ propName }) => {
    const { selectedOneOfSchema, oneOfSchemas, onSchemaChange, shouldRender } =  useOneOfField(propName);

    const onCleanInput = () => {
        onSchemaChange();
    };

    if (!shouldRender) {
        return null;
    }

    return (
        <SchemaList
            propName={propName}
            selectedSchema={selectedOneOfSchema}
            schemas={oneOfSchemas}
            onChange={onSchemaChange}
            onCleanInput={onCleanInput}
            aria-label={`${propName} oneof list`}
            data-testid={`${propName}__oneof-list`}
        >
            {selectedOneOfSchema && (
                <SchemaProvider schema={selectedOneOfSchema.schema}>
                    <AutoField propName={propName} />
                </SchemaProvider>
            )}
        </SchemaList>
    );
};
