import {ComponentsCatalog, ComponentsCatalogTypes} from "../model/catalog/catalog-index.ts";
import { CatalogKind } from "../model/catalog/catalog-kind.ts";
import { IComponentDefinition } from "../model/catalog/components-catalog.ts";
import {IProcessorDefinition} from "../model/catalog/processors-catalog.ts";
import { ILanguageDefinition } from "../model/catalog/languages-catalog.ts";
import { IDataformatDefinition } from "../model/catalog/dataformats-catalog.ts";
import {ILoadBalancerDefinition} from "../model/catalog/loadbalancers-catalog.ts";


// manage the catalogs
export class CatalogService {
    private static catalogs: ComponentsCatalog = {};

    static getCatalogByKey<CATALOG_KEY extends CatalogKind>(catalogKey: CATALOG_KEY): ComponentsCatalog[CATALOG_KEY] {
        return this.catalogs[catalogKey];
    }

    static setCatalogKey<CATALOG_KEY extends CatalogKind>(
        catalogKey: CATALOG_KEY,
        catalog?: ComponentsCatalog[CATALOG_KEY],
    ): void {
        this.catalogs[catalogKey] = catalog;
    }

    static getComponent(catalogKey: CatalogKind.Component, componentName?: string): IComponentDefinition | undefined;
    static getComponent(catalogKey: CatalogKind.Processor, componentName?: string): IProcessorDefinition | undefined;
    static getComponent(catalogKey: CatalogKind.Pattern, patternName?: string): IProcessorDefinition | undefined;
    static getComponent(catalogKey: CatalogKind.Entity, entityName?: string): IProcessorDefinition | undefined;
    static getComponent(catalogKey: CatalogKind.Language, languageName?: string): ILanguageDefinition | undefined;
    static getComponent(
        catalogKey: CatalogKind.Dataformat,
        dataformatName?: string,
    ): IDataformatDefinition | undefined;
    static getComponent(
        catalogKey: CatalogKind.Loadbalancer,
        loadBalancerName?: string,
    ): ILoadBalancerDefinition | undefined;
    static getComponent(catalogKey: CatalogKind, componentName?: string): ComponentsCatalogTypes | undefined;
    static getComponent(catalogKey: CatalogKind, componentName?: string): ComponentsCatalogTypes | undefined {
        if (componentName === undefined) return undefined;

        return this.catalogs[catalogKey]?.[componentName];
    }

    static getLanguageMap(): Record<string, ILanguageDefinition> {
        return this.catalogs[CatalogKind.Language] || {};
    }

    static getDataFormatMap(): Record<string, IDataformatDefinition> {
        return this.catalogs[CatalogKind.Dataformat] || {};
    }

    static getLoadBalancerMap(): Record<string, ILoadBalancerDefinition> {
        return this.catalogs[CatalogKind.Loadbalancer] || {};
    }

    /**
     * Public only as a convenience method for test
     * not meant to be used in production code
     */
    static clearCatalogs(): void {
        this.catalogs = {};
    }

    /** Method to return whether this is a Component  */
    static getCatalogLookup(componentName: string): {
        catalogKind: CatalogKind.Component;
        definition?: IComponentDefinition;
    };

    static getCatalogLookup(
        componentName: string,
    ): { catalogKind: CatalogKind; definition?: ComponentsCatalogTypes } | undefined {
        if (!componentName) {
            return undefined;
        }


        return {
            catalogKind: CatalogKind.Component,
            definition: this.getComponent(CatalogKind.Component, componentName),
        };
    }
}
