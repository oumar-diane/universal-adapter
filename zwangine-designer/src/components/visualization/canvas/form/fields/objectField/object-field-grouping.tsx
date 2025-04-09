import { FunctionComponent, useContext, useMemo } from 'react';
import {SchemaContext, SchemaProvider, FieldProps, AnyOfField, GroupFields, ObjectFieldInner} from "@/components/visualization";
import { FilteredFieldContext } from '@/providers';
import {getFieldGroupsV2, getFilteredProperties} from "@/lib/utility";

const SPACE_REGEX = /\s/g;

export const ObjectFieldGrouping: FunctionComponent<FieldProps> = ({ propName }) => {
    const { schema } = useContext(SchemaContext);
    const { filteredFieldText } = useContext(FilteredFieldContext);

    const groupedProperties = useMemo(() => {
        const cleanQueryTerm = filteredFieldText.replace(SPACE_REGEX, '').toLowerCase();
        const filteredProperties = getFilteredProperties(schema.properties, cleanQueryTerm);
        return getFieldGroupsV2(filteredProperties);
    }, [filteredFieldText, schema.properties]);

    const requiredProperties = Array.isArray(schema.required) ? schema.required : [];

    return (
        <>
            {/* Common properties */}
            <SchemaProvider schema={{ properties: groupedProperties.common }}>
                <ObjectFieldInner propName={propName} requiredProperties={requiredProperties} />
            </SchemaProvider>

            {/* AnyOf field */}
            {Array.isArray(schema.anyOf) && <AnyOfField propName={propName} anyOf={schema.anyOf} />}

            {/* Grouped properties */}
            <GroupFields propName={propName} groups={groupedProperties.groups} requiredProperties={requiredProperties} />
        </>
    );
};
