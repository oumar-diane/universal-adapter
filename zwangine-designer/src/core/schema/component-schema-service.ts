import { cloneDeep } from 'lodash';
import {
    getValue,
    isDefined,
    ParsedParameters,
    UriHelper,
} from '@/lib/utility';

import { ProcessorDefinition } from '@/@types';
import {VisualComponentSchema} from "../model/entity/base-visual-entity.ts";
import {IElementLookupResult, ProcessorStepsProperties} from '../model/component-type.ts';
import {NodeLabelType, SchemaDefinition} from '../model/schema/schema.ts';
import { CatalogKind } from '../model/catalog/catalog-kind.ts';
import { ComponentFilterService } from '../component-filter-service.ts';
import { CatalogService } from '../catalog/catalog-service.ts';

// manipulate the dsl
export class ComponentSchemaService {
    static DISABLED_SIBLING_STEPS = [
        'route',
        'from',
        'onWhen',
        'when',
        'otherwise',
        'doCatch',
        'doFinally',
        'intercept',
        'interceptFrom',
        'interceptSendToEndpoint',
        'onException',
        'onCompletion',
        ...ComponentFilterService.REST_DSL_METHODS,
    ];
    static DISABLED_REMOVE_STEPS = ['from', 'workflow'] as unknown as (keyof ProcessorDefinition)[];

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    static getVisualComponentSchema(path: string, dslAbstractRepresentation: any): VisualComponentSchema | undefined {
        const elementLookup = this.getComponentLookup(path, dslAbstractRepresentation);
        const updatedDslAbstractRepresentation = this.getUpdatedDefinition(elementLookup, dslAbstractRepresentation);

        return {
            schema: this.getSchema(elementLookup),
            definition: updatedDslAbstractRepresentation,
        };
    }

    // returns the component name and it processor name at the same time otherwise return the processor name juste if the path is the a processor
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    static getComponentLookup(path: string, dslAbstractRepresentation: any): IElementLookupResult {
        const splitPath = path.split('.');
        const lastPathSegment = splitPath[splitPath.length - 1];
        // the second argument is the radix which is the base of the number system eg: here we are in base 10
        const pathAsIndex = Number.parseInt(lastPathSegment, 10);

        /**
         * If the last path segment is NaN, it means this is a Processor
         * for instance, `from`, `otherwise` or `to` properties in a Workflow
         * and we can just return the path as the name of the component
         */
        if (Number.isNaN(pathAsIndex)) {
            return this.getElement(lastPathSegment as keyof ProcessorDefinition, dslAbstractRepresentation);
        }

        /**
         * The last path segment is a number, it means is an array of objects
         * and we need to look for the previous path segment to get the name of the processor
         * for instance, a `when` property in a `Choice` processor
         */
        const previousPathSegment = splitPath[splitPath.length - 2];
        return this.getElement(previousPathSegment as keyof ProcessorDefinition, dslAbstractRepresentation);
    }

    static getNodeLabel(
        elementLookup: IElementLookupResult,
        dslAbstractRepresentation?: any,
        labelType?: NodeLabelType,
    ): string {
        const id: string | undefined = getValue(dslAbstractRepresentation, 'id');
        if (labelType === NodeLabelType.Id && id) {
            return id;
        }

        const description: string | undefined = getValue(dslAbstractRepresentation, 'description');
        if (description) {
            return description;
        }

        const semanticString = UriHelper.getSemanticString(elementLookup, dslAbstractRepresentation);
        if (elementLookup.componentName !== undefined) {
            return semanticString ?? elementLookup.componentName;
        }

        const uriString = UriHelper.getUriString(dslAbstractRepresentation);
        switch (elementLookup.processorName) {
            case 'workflow' as keyof ProcessorDefinition:
            case 'errorHandler' as keyof ProcessorDefinition:
            case 'onException' as keyof ProcessorDefinition:
            case 'onCompletion' as keyof ProcessorDefinition:
            case 'intercept' as keyof ProcessorDefinition:
            case 'interceptFrom' as keyof ProcessorDefinition:
            case 'interceptSendToEndpoint' as keyof ProcessorDefinition:
            case 'step':
                return elementLookup.processorName;

            case 'from' as keyof ProcessorDefinition:
                return uriString ?? 'from: Unknown';

            case 'to':
            case 'toD':
            case 'poll':
                return semanticString ?? uriString ?? elementLookup.processorName;

            default:
                return elementLookup.processorName;
        }
    }

    static getNodeTitle(elementLookup: IElementLookupResult): string {
        if (elementLookup.componentName !== undefined) {
            const catalogLookup = CatalogService.getCatalogLookup(elementLookup.componentName);
            if (catalogLookup.catalogKind === CatalogKind.Component) {
                return catalogLookup.definition?.component.title ?? elementLookup.componentName;
            }
        }

        const catalogLookup = CatalogService.getComponent(CatalogKind.Processor, elementLookup.processorName);

        return catalogLookup?.model.title ?? elementLookup.processorName;
    }

    static getTooltipContent(elementLookup: IElementLookupResult): string {
        if (elementLookup.componentName !== undefined) {
            const catalogLookup = CatalogService.getCatalogLookup(elementLookup.componentName);
            if (catalogLookup.catalogKind === CatalogKind.Component) {
                return catalogLookup.definition?.component.description ?? elementLookup.componentName;
            }
        }

        const schema = this.getSchema(elementLookup);
        if (schema.description !== undefined) {
            return schema.description;
        }

        return elementLookup.processorName;
    }

    static canHavePreviousStep(processorName: keyof ProcessorDefinition): boolean {
        return !this.DISABLED_SIBLING_STEPS.includes(processorName);
    }

    static canReplaceStep(processorName: keyof ProcessorDefinition): boolean {
        return (
            processorName === ('from' as keyof ProcessorDefinition) || !this.DISABLED_SIBLING_STEPS.includes(processorName)
        );
    }

    static getProcessorStepsProperties(processorName: keyof ProcessorDefinition): ProcessorStepsProperties[] {
        switch (processorName) {
            /** choice */ case 'when':
            /** choice */ case 'otherwise':
            /** doTry */ case 'doCatch':
            /** doTry */ case 'doFinally':
            case 'aggregate':
            case 'filter':
            case 'loadBalance':
            case 'loop':
            case 'multicast':
            case 'onFallback':
            case 'pipeline':
            case 'resequence':
            case 'saga':
            case 'split':
            case 'step':
            case 'whenSkipSendToEndpoint':
            case 'from' as keyof ProcessorDefinition:
            case /** routeConfiguration */ 'intercept' as keyof ProcessorDefinition:
            case /** routeConfiguration */ 'interceptFrom' as keyof ProcessorDefinition:
            case /** routeConfiguration */ 'interceptSendToEndpoint' as keyof ProcessorDefinition:
            case /** routeConfiguration */ 'onException' as keyof ProcessorDefinition:
            case /** routeConfiguration */ 'onCompletion' as keyof ProcessorDefinition:
                return [{ name: 'steps', type: 'branch' }];

            case 'circuitBreaker':
                return [
                    { name: 'steps', type: 'branch' },
                    { name: 'onFallback', type: 'single-clause' },
                ];

            case 'choice':
                return [
                    { name: 'when', type: 'array-clause' },
                    { name: 'otherwise', type: 'single-clause' },
                ];

            case 'doTry':
                return [
                    { name: 'steps', type: 'branch' },
                    { name: 'doCatch', type: 'array-clause' },
                    { name: 'doFinally', type: 'single-clause' },
                ];

            case 'routeConfiguration' as keyof ProcessorDefinition:
                return [
                    { name: 'intercept', type: 'array-clause' },
                    { name: 'interceptFrom', type: 'array-clause' },
                    { name: 'interceptSendToEndpoint', type: 'array-clause' },
                    { name: 'onException', type: 'array-clause' },
                    { name: 'onCompletion', type: 'array-clause' },
                ];

            case 'rest' as keyof ProcessorDefinition:
                return ComponentFilterService.REST_DSL_METHODS.map((method: any) => ({ name: method, type: 'array-clause' }));

            default:
                return [];
        }
    }

    static getIconName(elementLookup: IElementLookupResult): string | undefined {
        if (isDefined(elementLookup.componentName)) {
            const catalogLookup = CatalogService.getCatalogLookup(elementLookup.componentName);
            if (isDefined(catalogLookup.definition)) {
                return elementLookup.componentName;
            }
        }

        if (
            isDefined(elementLookup.processorName) &&
            !isDefined(elementLookup.componentName) &&
            isDefined(CatalogService.getComponent(CatalogKind.Processor, elementLookup.processorName))
        ) {
            return elementLookup.processorName;
        }

        return '';
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    static getMultiValueSerializedDefinition(path: string, definition: any): ParsedParameters | undefined {
        const elementLookup = this.getComponentLookup(path, definition);
        if (elementLookup.componentName === undefined) {
            return definition;
        }

        const catalogLookup = CatalogService.getCatalogLookup(elementLookup.componentName);
        if (catalogLookup.catalogKind === CatalogKind.Component) {
            const multiValueParameters: Map<string, string> = new Map<string, string>();
            if (catalogLookup.definition?.properties !== undefined) {
                Object.entries(catalogLookup.definition.properties).forEach(([key, value]) => {
                    if (value.multiValue) multiValueParameters.set(key, value.prefix!);
                });
            }
            const defaultMultiValues: ParsedParameters = {};
            const filteredParameters = definition.parameters;

            if (definition.parameters !== undefined) {
                Object.keys(definition.parameters).forEach((key) => {
                    if (multiValueParameters.has(key)) {
                        if (definition.parameters[key] === undefined) {
                            return;
                        }
                        Object.keys(definition.parameters[key]).forEach((subKey) => {
                            defaultMultiValues[multiValueParameters.get(key) + subKey] = definition.parameters[key][subKey];
                        });
                        delete filteredParameters[key];
                    }
                });
            }
            return Object.assign({}, definition, { parameters: { ...filteredParameters, ...defaultMultiValues } });
        }
        return definition;
    }

    /**
     * If the processor is a `from` or `to` processor, we need to extract the component name from the uri property
     * and return both the processor name and the underlying component name to build the combined schema
     */
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private static getElement(processorName: keyof ProcessorDefinition, dslAbstractRepresentation: any): IElementLookupResult {
        if (!isDefined(dslAbstractRepresentation)) {
            return { processorName };
        }

        switch (processorName) {
            case 'from' as keyof ProcessorDefinition:
                return {
                    processorName,
                    componentName: this.getComponentNameFromUri(dslAbstractRepresentation?.uri),
                };

            case 'to':
            case 'toD':
            case 'poll':
                /** The To processor is using `to: timer:tick?period=1000` form */
                if (typeof dslAbstractRepresentation === 'string') {
                    return {
                        processorName,
                        componentName: this.getComponentNameFromUri(dslAbstractRepresentation),
                    };
                }

                /** The To processor is using `to: { uri: 'timer:tick?period=1000' }` form */
                return {
                    processorName,
                    componentName: this.getComponentNameFromUri(dslAbstractRepresentation?.uri),
                };

            default:
                return { processorName };
        }
    }

    /**
     * Extract the component name from the endpoint uri
     * An URI is composed by a component name and query parameters, separated by a colon
     * this is used only when we have from/to/toD/pool processorsors
     * For instance:
     *    - `log:MyLogger`
     *    - `timer:tick?period=1000`
     *    - `file:inbox?fileName=orders.txt&noop=true`
     *    - `kamelet:kafka-not-secured-sink?topic=foobar&bootstrapServers=localhost`
     */
    static getComponentNameFromUri(uri: string): string | undefined {
        if (!uri) {
            return undefined;
        }
        const uriParts = uri.split(':');
        return uriParts[0];
    }

    private static getSchema(elementLookup: IElementLookupResult): SchemaDefinition['schema'] {
        let catalogKind: CatalogKind;
        switch (elementLookup.processorName) {
            case 'workflow' as keyof ProcessorDefinition:
            case 'intercept' as keyof ProcessorDefinition:
            case 'interceptFrom' as keyof ProcessorDefinition:
            case 'interceptSendToEndpoint' as keyof ProcessorDefinition:
            case 'onException' as keyof ProcessorDefinition:
            case 'onCompletion' as keyof ProcessorDefinition:
            case 'from' as keyof ProcessorDefinition:
                catalogKind = CatalogKind.Entity;
                break;
            default:
                catalogKind = CatalogKind.Pattern;
        }

        const processorDefinition = CatalogService.getComponent(catalogKind, elementLookup.processorName);

        if (processorDefinition === undefined) return {} as unknown as SchemaDefinition['schema'];

        let schema = {} as unknown as SchemaDefinition['schema'];
        if (processorDefinition.propertiesSchema !== undefined) {
            schema = cloneDeep(processorDefinition.propertiesSchema);
        }

        if (elementLookup.componentName !== undefined) {
            const catalogLookup = CatalogService.getCatalogLookup(elementLookup.componentName);
            const componentSchema: SchemaDefinition['schema'] = catalogLookup.definition?.propertiesSchema ?? ({} as unknown as SchemaDefinition['schema']);

            // Filter out producer/consumer properties depending upon the endpoint usage
            const actualComponentProperties = Object.fromEntries(
                Object.entries(componentSchema.properties ?? {}).filter((property) => {
                    if (elementLookup.processorName === ('from' as keyof ProcessorDefinition)) {
                        return !property[1].$comment?.includes('producer');
                    } else {
                        return !property[1].$comment?.includes('consumer');
                    }
                }),
            );

            if (catalogLookup.definition !== undefined) {
                schema.properties!.parameters = {
                    type: 'object',
                    title: 'Endpoint Properties',
                    description: 'Endpoint properties description',
                    properties: actualComponentProperties,
                    required: componentSchema.required,
                };
            }
        }

        return schema;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private static getUpdatedDefinition(elementLookup: IElementLookupResult, dslAbstractRepresentation: any) {
        /** Clone the original dslAbstractRepresentation since we want to preserve the original one, until the form is changed */
        let updatedDslAbstractRepresentation = cloneDeep(dslAbstractRepresentation);
        switch (elementLookup.processorName) {
            case 'to':
            case 'toD':
                if (typeof dslAbstractRepresentation === 'string') {
                    updatedDslAbstractRepresentation = { uri: dslAbstractRepresentation };
                }
                break;

            case 'log':
                if (typeof dslAbstractRepresentation === 'string') {
                    updatedDslAbstractRepresentation = { message: dslAbstractRepresentation };
                }
                break;
        }

        if (elementLookup.componentName !== undefined) {
            updatedDslAbstractRepresentation.parameters = updatedDslAbstractRepresentation.parameters ?? {};
            this.applyParametersFromSyntax(elementLookup.componentName, updatedDslAbstractRepresentation);
            this.readMultiValue(elementLookup.componentName, updatedDslAbstractRepresentation);
        }

        return updatedDslAbstractRepresentation;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private static readMultiValue(componentName: string, dslAbstractRepresentation: any) {
        const catalogLookup = CatalogService.getCatalogLookup(componentName);

        const multiValueParameters: Map<string, string> = new Map<string, string>();
        if (catalogLookup !== undefined && catalogLookup.definition?.properties !== undefined) {
            Object.entries(catalogLookup.definition.properties).forEach(([key, value]) => {
                if (value.multiValue) multiValueParameters.set(key, value.prefix!);
            });
        }
        if (multiValueParameters.size > 0) {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const parameters: any = {};
            const filteredParameters = dslAbstractRepresentation.parameters;

            multiValueParameters.forEach((value, key) => {
                const nestParameters: ParsedParameters = {};

                Object.entries(dslAbstractRepresentation.parameters).forEach(([paramKey, paramValue]) => {
                    if (paramKey.startsWith(value)) {
                        nestParameters[paramKey.replace(value, '')] = paramValue as string;
                        delete filteredParameters[paramKey];
                    }
                    parameters[key] = { ...nestParameters };
                });
            });
            Object.assign(dslAbstractRepresentation, { parameters: { ...filteredParameters, ...parameters } });
        }
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private static applyParametersFromSyntax(componentName: string, dslAbstractRepresentation: any) {
        const catalogLookup = CatalogService.getCatalogLookup(componentName);
        if (catalogLookup === undefined) return;

        const [pathUri, queryUri] = dslAbstractRepresentation.uri?.split('?') ?? [undefined, undefined];
        if (queryUri) {
            dslAbstractRepresentation.uri = pathUri;
            Object.assign(dslAbstractRepresentation.parameters, UriHelper.getParametersFromQueryString(queryUri));
        }

        if (pathUri && catalogLookup.catalogKind === CatalogKind.Component) {
            const requiredParameters: string[] = [];
            if (catalogLookup.definition?.properties !== undefined) {
                Object.entries(catalogLookup.definition.properties).forEach(([key, value]) => {
                    if (value.required) {
                        requiredParameters.push(key);
                    }
                });
            }

            const parametersFromSyntax = UriHelper.getParametersFromPathString(
                catalogLookup.definition?.component.syntax,
                dslAbstractRepresentation?.uri,
                { requiredParameters },
            );
            dslAbstractRepresentation.uri = this.getComponentNameFromUri(dslAbstractRepresentation.uri);
            Object.assign(dslAbstractRepresentation.parameters, parametersFromSyntax);
        }
    }

    static canBeDisabled(processorName: keyof ProcessorDefinition): boolean {
        const processorDefinition = CatalogService.getComponent(CatalogKind.Processor, processorName);

        return Object.keys(processorDefinition?.properties ?? {}).includes('disabled');
    }
}
