import {IPropertiesRow, IPropertiesTable, PropertiesHeaders, PropertiesTableType} from '@/components/propertyModal';
import {
    IComponentApi,
    IComponentApiKind,
    IComponentApiProperty, IComponentHeader,
    IComponentProperty
} from '@/core/model/catalog/components-catalog';
import {IProcessorProperty} from "@/core/model/catalog/processors-catalog.ts";


export type IComponentCommonProperties = IComponentProperty | IComponentHeader;

export interface IPropertiesTableFilter<T> {
    filterKey: keyof T;
    filterValue: T[keyof T];
}

const simpleClassNameRegex = RegExp('^[A-Za-z0-9]+$');
const fullyQualifiedClassNameRegex = RegExp('^([a-z0-9A-Z._]+)\\.([A-Z][a-zA-Z0-9]+)(<)?.*$');

/**
 * Get class name from fully qualified name
 * e.g.
 * String => String
 * java.util.Set<java.nio.file.OpenOption> => Set
 * org.apache.ua.component.as2.AS2Component => AS2Component
 * org.hl7.fhir.instance.model.api.IPrimitiveType<java.util.Date> => IPrimitiveType
 * @param fullyQualifiedName - Fully qualified name
 * @returns Class name only
 */
const getClassNameOnly = (fullyQualifiedName: string): string => {
    if (simpleClassNameRegex.test(fullyQualifiedName)) {
        // it is already class name only
        return fullyQualifiedName;
    }
    const regex = fullyQualifiedClassNameRegex.exec(fullyQualifiedName);
    if (regex && regex.length > 0) {
        return regex[2];
    } else {
        console.log('[WARN] Not able to parse this fully qualified name: ' + fullyQualifiedName);
        return fullyQualifiedName;
    }
};
// only for testing private function in jest
export const getClassNameOnlyFunctionExportedForTesting = getClassNameOnly;

const fullFillFilter = <T>(value: T, filter: IPropertiesTableFilter<T>): boolean => {
    return value[filter.filterKey] === filter.filterValue;
};

export const componentPropertiesToTable = (
    properties: Record<string, IComponentCommonProperties>,
    filter?: IPropertiesTableFilter<IComponentCommonProperties>,
): IPropertiesTable => {
    const propertiesRows: IPropertiesRow[] = [];
    for (const [key, value] of Object.entries(properties)) {
        if (filter && !fullFillFilter(value, filter)) continue;
        propertiesRows.push({
            name: key,
            description: value.description,
            default: value.defaultValue,
            type: getClassNameOnly(value.javaType),
            rowAdditionalInfo: {
                required: value.required,
                group: value.group,
                autowired: value.autowired,
                enum: value.enum,
            },
        });
    }
    return {
        type: PropertiesTableType.Simple,
        headers: [PropertiesHeaders.Name, PropertiesHeaders.Description, PropertiesHeaders.Default, PropertiesHeaders.Type],
        rows: propertiesRows,
    };
};

const getApiType = (consumerOnly: boolean, producerOnly: boolean): string => {
    return consumerOnly && producerOnly ? 'Both' : consumerOnly ? 'Consumer' : producerOnly ? 'Producer' : 'Both';
};

interface IComponentApisToTableProps {
    apis: Record<string, IComponentApi>;
    apiProperties: Record<string, IComponentApiProperty>;
}

export const componentApisToTable = (
    uaApisInfo: IComponentApisToTableProps,
    filter?: IPropertiesTableFilter<IComponentApi>,
): IPropertiesTable => {
    const apisRows: IPropertiesRow[] = [];
    //go through APIs
    for (const [apiName, api] of Object.entries(uaApisInfo.apis)) {
        if (filter && !fullFillFilter(api, filter)) continue;
        const methodsRows: IPropertiesRow[] = [];
        //go through Methods
        for (const [methodName, method] of Object.entries(api.methods)) {
            const propertiesRows: IPropertiesRow[] = [];
            //go through method properties
            const properties = uaApisInfo.apiProperties[apiName]?.methods[methodName]?.properties;
            if (!properties) continue; // in case this method doesn't have any property
            for (const [propertyName, property] of Object.entries(properties)) {
                propertiesRows.push({
                    name: propertyName,
                    description: property.description,
                    type: getClassNameOnly(property.javaType),
                    rowAdditionalInfo: {
                        required: property.required,
                        autowired: property.autowired,
                        enum: property.enum,
                        apiKind: IComponentApiKind.PARAM,
                    },
                });
            }
            methodsRows.push({
                name: methodName,
                description: method.description,
                type: '',
                children: propertiesRows,
                rowAdditionalInfo: {
                    apiKind: IComponentApiKind.METHOD,
                },
            });
        }
        apisRows.push({
            name: apiName,
            description: api.description,
            type: getApiType(api.consumerOnly, api.producerOnly),
            children: methodsRows,
            rowAdditionalInfo: {
                apiKind: IComponentApiKind.API,
            },
        });
    }
    return {
        type: PropertiesTableType.Tree,
        headers: [PropertiesHeaders.Name, PropertiesHeaders.Description, PropertiesHeaders.Type],
        rows: apisRows,
    };
};

export const processorPropertiesToTable = (
    properties: Record<string, IProcessorProperty>,
    filter?: IPropertiesTableFilter<IProcessorProperty>,
): IPropertiesTable => {
    const propertiesRows: IPropertiesRow[] = [];
    for (const [key, value] of Object.entries(properties)) {
        if (filter && !fullFillFilter(value, filter)) continue;
        propertiesRows.push({
            name: key,
            default: value.defaultValue,
            type: getClassNameOnly(value.javaType),
            description: value.description,
            rowAdditionalInfo: {
                required: value.required,
                autowired: value.autowired,
                enum: value.enum,
            },
        });
    }
    return {
        type: PropertiesTableType.Simple,
        headers: [PropertiesHeaders.Name, PropertiesHeaders.Default, PropertiesHeaders.Type, PropertiesHeaders.Description],
        rows: propertiesRows,
    };
};

