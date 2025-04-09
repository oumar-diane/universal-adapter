
import {CatalogDefinition, CatalogDefinitionEntry} from '@/@types/catalog-index';
import { IProcessorDefinition } from './processors-catalog.ts';
import { IComponentDefinition } from './components-catalog.ts';
import { ILanguageDefinition } from './languages-catalog.ts';
import { IDataformatDefinition } from './dataformats-catalog.ts';
import { ILoadBalancerDefinition } from './loadbalancers-catalog.ts';
import { CatalogKind } from './catalog-kind.ts';

export interface CatalogIndex extends Omit<CatalogDefinition, 'catalogs'> {
    catalogs: {
        models: CatalogDefinitionEntry;
        components: CatalogDefinitionEntry;
        languages: CatalogDefinitionEntry;
        dataformats: CatalogDefinitionEntry;
        patterns: CatalogDefinitionEntry;
        entities: CatalogDefinitionEntry;
        loadbalancers: CatalogDefinitionEntry;
    };
}

export type ComponentsCatalogTypes =
    | IComponentDefinition
    | IProcessorDefinition
    | ILanguageDefinition
    | IDataformatDefinition
    | ILoadBalancerDefinition

export type DefinedComponent = {
    name: string;
    type: CatalogKind;
    definition?: ComponentsCatalogTypes;
    defaultValue?: object;
};

export interface ComponentsCatalog {
    [CatalogKind.Component]?: Record<string, IComponentDefinition>;
    [CatalogKind.Processor]?: Record<string, IProcessorDefinition>;
    [CatalogKind.Pattern]?: Record<string, IProcessorDefinition>;
    [CatalogKind.Entity]?: Record<string, IProcessorDefinition>;
    [CatalogKind.Language]?: Record<string, ILanguageDefinition>;
    [CatalogKind.Dataformat]?: Record<string, IDataformatDefinition>;
    [CatalogKind.Loadbalancer]?: Record<string, ILoadBalancerDefinition>;
}
