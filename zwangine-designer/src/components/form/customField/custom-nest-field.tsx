
import { Card, CardBody, CardHeader, CardTitle } from '@patternfly/react-core';
import { useContext } from 'react';
import { HTMLFieldProps, connectField, filterDOMProps } from 'uniforms';
import './custom-nest-field.scss';
import {getFieldGroups, getFilteredProperties } from '@/lib/utility';
import {CustomAutoField, CustomExpandableSection} from "@/components/form";
import {FilteredFieldContext} from "@/providers";
import {SchemaDefinition} from "@/core/model/schema/schema.ts";

export type CustomNestFieldProps = HTMLFieldProps<
    object,
    HTMLDivElement,
    {
        properties?: Record<string, unknown>;
        helperText?: string;
        itemProps?: object;
    }
>;

export const CustomNestField = connectField(
    ({
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         children,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         error,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         errorMessage,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         fields,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         itemProps,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         label,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         name,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         showInlineError,
         // eslint-disable-next-line @typescript-eslint/no-unused-vars
         disabled,
         ...props
     }: CustomNestFieldProps) => {
        const { filteredFieldText, isGroupExpanded } = useContext(FilteredFieldContext);
        const cleanQueryTerm = filteredFieldText.replace(/\s/g, '').toLowerCase();
        const filteredProperties = getFilteredProperties(
            props.properties as SchemaDefinition['schema']['properties'],
            cleanQueryTerm,
        );
        const propertiesArray = getFieldGroups(filteredProperties);
        if (propertiesArray.common.length === 0 && Object.keys(propertiesArray.groups).length === 0) return null;

        return (
            <Card className="custom-nest-field" data-testid="nest-field" {...filterDOMProps(props)}>
                <CardHeader>
                    <CardTitle>{label}</CardTitle>
                </CardHeader>
                <CardBody>
                    {propertiesArray.common.map((field) => (
                        <CustomAutoField key={field} name={field} />
                    ))}
                </CardBody>

                {Object.entries(propertiesArray.groups)
                    .sort((a, b) => (a[0] === 'advanced' ? 1 : b[0] === 'advanced' ? -1 : 0))
                    .map(([groupName, groupFields]) => (
                        <CustomExpandableSection
                            key={`${groupName}-section-toggle`}
                            groupName={groupName}
                            isGroupExpanded={isGroupExpanded}
                        >
                            <CardBody>
                                {groupFields.map((field) => (
                                    <CustomAutoField key={field} name={field} />
                                ))}
                            </CardBody>
                        </CustomExpandableSection>
                    ))}
            </Card>
        );
    },
);
