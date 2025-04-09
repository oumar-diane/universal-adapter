import {SchemaDefinition} from "../schema/schema.ts";
import {PropertyCommon} from "../properties-common.ts";
import {CatalogKind} from "./catalog-kind.ts";

export interface IComponentDefinition {
    component: IComponent;
    componentProperties: Record<string, IComponentProperty>;
    properties: Record<string, IComponentProperty>;
    propertiesSchema: SchemaDefinition['schema'];
    headers?: Record<string, IComponentHeader>;
    apis?: Record<string, IComponentApi>;
    apiProperties?: Record<string, IComponentApiProperty>;
}

export interface IComponent {
    kind: CatalogKind.Component;
    name: string;
    title: string;
    description?: string;
    deprecated: boolean;
    firstVersion?: string;
    label: string;
    javaType?: string;
    supportLevel?: string;
    groupId?: string;
    artifactId?: string;
    version: string;
    scheme?: string;
    extendsScheme?: string;
    syntax?: string;
    async?: boolean;
    api?: boolean;
    consumerOnly?: boolean;
    producerOnly?: boolean;
    lenientProperties?: boolean;
    provider?: string;
}

// these interfaces don't contain all properties which are save in the component json object. If you need some new, add it here
export interface IComponentProperty extends PropertyCommon {
    group: string;
    type: string;
}

export interface IComponentHeader extends PropertyCommon {
    group: string;
    constantName: string;
}

// e.g. for as2/twilio component
export interface IComponentApi {
    consumerOnly: boolean;
    producerOnly: boolean;
    description: string;
    methods: Record<string, IComponentApiMethod>;
}

export interface IComponentApiMethod {
    description: string;
    signatures: string[];
}

export interface IComponentApiProperty {
    methods: Record<string, IComponentApiPropertyMethod>;
}

export interface IComponentApiPropertyMethod {
    properties: Record<string, IComponentProperty>; // api.method.property is same as camelcomponentproperty
}

export enum IComponentApiKind {
    API = 'Api',
    METHOD = 'Method',
    PARAM = 'Param',
}
