import {isDefined, OneOfSchemas } from '@/lib/utility';
import { FormGroup } from '@patternfly/react-core';
import { FunctionComponent, PropsWithChildren, useCallback, useMemo } from 'react';
import {FieldProps} from '../../typing'
import { UaSchemaDefinition } from '@/core';
import {SimpleSelector, Typeahead, TypeaheadItem} from '@/components/typeahead';

interface SchemaList extends FieldProps {
    selectedSchema: OneOfSchemas | undefined;
    schemas: OneOfSchemas[];
    onChange: (schema: OneOfSchemas | undefined) => void;
    onCleanInput?: () => void;
    placeholder?: string;
}

export const SchemaList: FunctionComponent<PropsWithChildren<SchemaList>> = ({
                                                                                 propName,
                                                                                 selectedSchema,
                                                                                 schemas,
                                                                                 onChange,
                                                                                 onCleanInput,
                                                                                 placeholder,
                                                                                 children,
                                                                             }) => {
    const items: TypeaheadItem<UaSchemaDefinition['schema']>[] = useMemo(
        () => schemas.map(({ name, description, schema }) => ({ name, description, value: schema })),
        [schemas],
    );
    const useTypeahead = items.length > 5;

    const selectedItem = useMemo(() => {
        if (!selectedSchema) {
            return undefined;
        }

        return items.find((item) => item.name === selectedSchema.name);
    }, [items, selectedSchema]);

    const onItemChange = useCallback(
        (item?: TypeaheadItem<OneOfSchemas>) => {
            if (!isDefined(item)) {
                onChange(undefined);
                return;
            }

            onChange({
                name: item.name,
                description: item.description,
                schema: item.value,
            });
        },
        [onChange],
    );

    return (
        <>
            <FormGroup isStack hasNoPaddingTop fieldId={propName} role="group">
                {useTypeahead ? (
                    <Typeahead
                        selectedItem={selectedItem}
                        items={items}
                        id={propName}
                        placeholder={placeholder}
                        onChange={onItemChange}
                        onCleanInput={onCleanInput}
                    />
                ) : (
                    <SimpleSelector
                        selectedItem={selectedItem}
                        items={items}
                        id={propName}
                        onChange={onItemChange}
                    />
                )}
            </FormGroup>

            {children}
        </>
    );
};
