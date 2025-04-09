import {IPropertiesTab, IPropertiesTable } from "@/components/propertyModal";
import {
    IPropertiesTableFilter,
    componentApisToTable,
    componentPropertiesToTable,
    processorPropertiesToTable
} from "@/lib/ua-utils/index.ts";
import { isDefined } from "@/lib/utility";
import {IComponentDefinition} from "@/core/model/catalog/components-catalog.ts";
import {IProcessorDefinition} from "@/core/model/catalog/processors-catalog.ts";

interface IPropertiesTransformToTable<K, T> {
    transformFunction: (properties: K, filter?: IPropertiesTableFilter<T>) => IPropertiesTable;
    objectToTransform: K;
    tableCaption?: string;
    filter?: IPropertiesTableFilter<T>;
}

/**
 * This function transforms properties data (from ua-component, ua-processor, kamelets) into one or more tables which are added into the final IPropertiesTab.
 *
 * @param transformationsData object which contains function which is used for transfromation properties object into IPropertiesTable, the functions are defined in `ua-to-table.adapter.ts` file, optionally filter and table caption
 * @param tabTitle - Title of the Tab
 * @returns Tab data which will be use for rendering one Tab
 */
const transformPropertiesIntoTab = <K, T>(
    transformationsData: IPropertiesTransformToTable<K, T>[],
    tabTitle: string,
): IPropertiesTab | undefined => {
    const tables = [];
    for (const transformationData of transformationsData) {
        const table = transformationData.transformFunction(transformationData.objectToTransform, transformationData.filter);
        if (table.rows.length == 0) continue; // we don't care about empty table
        if (transformationData.tableCaption)
            table.caption = transformationData.tableCaption + ' (' + table.rows.length + ')';
        tables.push(table);
    }
    if (tables.length == 0) return undefined;

    let allRowsInAllTables = 0;
    tables.forEach((table) => {
        allRowsInAllTables += table.rows.length;
    });
    return {
        rootName: tabTitle + ' (' + allRowsInAllTables + ')',
        tables: tables,
    };
};

export const transformComponentIntoTab = (
    componentDef: IComponentDefinition | undefined,
): IPropertiesTab[] => {
    if (!isDefined(componentDef)) return [];

    const finalTabs: IPropertiesTab[] = [];
    let tab = transformPropertiesIntoTab(
        [
            {
                transformFunction: componentPropertiesToTable,
                objectToTransform: componentDef.componentProperties,
            },
        ],
        'Component Options',
    );
    if (tab) finalTabs.push(tab);

    // properties, contains 2 subtables divided according to Kind
    tab = transformPropertiesIntoTab(
        [
            {
                transformFunction: componentPropertiesToTable,
                objectToTransform: componentDef.properties,
                filter: {
                    filterKey: 'kind',
                    filterValue: 'path',
                },
                tableCaption: 'path parameters',
            },
            {
                transformFunction: componentPropertiesToTable,
                objectToTransform: componentDef.properties,
                filter: {
                    filterKey: 'kind',
                    filterValue: 'parameter',
                },
                tableCaption: 'query parameters',
            },
        ],
        'Endpoint Options',
    );
    if (tab) finalTabs.push(tab);

    // headers, only if exists
    if (componentDef.headers) {
        const tab = transformPropertiesIntoTab(
            [
                {
                    transformFunction: componentPropertiesToTable,
                    objectToTransform: componentDef.headers,
                },
            ],
            'Message Headers',
        );
        if (tab) finalTabs.push(tab);
    }

    // apis, only if exists
    if (componentDef.apis) {
        const tab = transformPropertiesIntoTab(
            [
                {
                    transformFunction: componentApisToTable,
                    objectToTransform: { apis: componentDef.apis!, apiProperties: componentDef.apiProperties! },
                },
            ],
            'APIs',
        );
        if (tab) finalTabs.push(tab);
    }
    return finalTabs;
};

export const transformProcessorComponentIntoTab = (
    processorDef: IProcessorDefinition | undefined,
): IPropertiesTab[] => {
    if (!isDefined(processorDef)) return [];

    const finalTabs: IPropertiesTab[] = [];
    const tab = transformPropertiesIntoTab(
        [
            {
                transformFunction: processorPropertiesToTable,
                objectToTransform: processorDef.properties,
            },
        ],
        'Options',
    );
    if (tab) finalTabs.push(tab);
    return finalTabs;
};

