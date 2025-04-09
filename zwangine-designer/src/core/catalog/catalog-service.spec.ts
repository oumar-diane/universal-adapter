import {it , beforeEach,expect, afterEach, describe} from "vitest"

import {getCatalogMap} from "@/stubs/test-load-catalog.ts";
import catalogDefinition from '@/assets/resources/catalog-index.json';
import { IComponentDefinition } from "../model/catalog/components-catalog.ts";
import { IDataformatDefinition } from "../model/catalog/dataformats-catalog.ts";
import { ILanguageDefinition } from "../model/catalog/languages-catalog.ts";
import { ILoadBalancerDefinition } from "../model/catalog/loadbalancers-catalog.ts";
import {CatalogIndex, CatalogKind, CatalogService} from "@/core";


let componentCatalogMap: Record<string, IComponentDefinition>;
let dataformatsCatalogMap: Record<string, IDataformatDefinition>;
let languagesCatalogMap: Record<string, ILanguageDefinition>;
let loadbalancersMap: Record<string, ILoadBalancerDefinition>;

beforeEach(async () => {
    const catalogsMap = await getCatalogMap(catalogDefinition as CatalogIndex);
    componentCatalogMap = catalogsMap.componentCatalogMap;
    dataformatsCatalogMap = catalogsMap.dataformatCatalog;
    languagesCatalogMap = catalogsMap.languageCatalog;
    loadbalancersMap = catalogsMap.loadbalancerCatalog;
    CatalogService.setCatalogKey(CatalogKind.Component, catalogsMap.componentCatalogMap);

})

afterEach(() => {

    CatalogService.clearCatalogs();
});

describe('getCatalogByKey', () => {
    it('should return the catalog', () => {
        const result = CatalogService.getCatalogByKey(CatalogKind.Component);

        expect(result).toEqual(componentCatalogMap);
    });
});

describe('getComponent', () => {
    it('should return the component', () => {
        const component = CatalogService.getComponent(CatalogKind.Component, 'timer');

        expect(component?.component.name).toEqual('timer');
        expect(component).toEqual((componentCatalogMap as Record<string, IComponentDefinition>).timer);
    });

    it('should return `undefined` for an `undefined` component name', () => {
        const component = CatalogService.getComponent(CatalogKind.Component);
        expect(component).toBeUndefined();
    });
});

describe('getLanguageMap', () => {
    it('should return an empty object if there is no language map', () => {
        const map = CatalogService.getLanguageMap();
        expect(map).toEqual({});
    });

    it('should return a language map', () => {
        CatalogService.setCatalogKey(
            CatalogKind.Language,
            languagesCatalogMap as unknown as Record<string, ILanguageDefinition>,
        );

        const map = CatalogService.getLanguageMap();
        expect(map).toEqual(languagesCatalogMap);
    });
});

describe('getDataFormatMap', () => {
    it('should return an empty object if there is no data format map', () => {
        const map = CatalogService.getDataFormatMap();
        expect(map).toEqual({});
    });

    it('should return a data format map', () => {
        CatalogService.setCatalogKey(
            CatalogKind.Dataformat,
            dataformatsCatalogMap as unknown as Record<string, ILanguageDefinition>,
        );

        const map = CatalogService.getDataFormatMap();
        expect(map).toEqual(dataformatsCatalogMap);
    });
});

describe('getLoadBalancerMap', () => {
    it('should return an empty object if there is no load balancer map', () => {
        const map = CatalogService.getLoadBalancerMap();
        expect(map).toEqual({});
    });

    it('should return a load balancer map', () => {
        CatalogService.setCatalogKey(
            CatalogKind.Loadbalancer,
            loadbalancersMap as unknown as Record<string, ILanguageDefinition>,
        );

        const map = CatalogService.getLoadBalancerMap();
        expect(map).toEqual(loadbalancersMap);
    });
});

describe('getCatalogLookup', () => {
    it('should return `undefined` for an empty string component name', () => {
        const lookup = CatalogService.getCatalogLookup('');

        expect(lookup).toBeUndefined();
    });

    it('should return a component from the catalog lookup', () => {
        const lookup = CatalogService.getCatalogLookup('timer');

        expect(lookup).toEqual({
            catalogKind: CatalogKind.Component,
            definition: (componentCatalogMap as Record<string, unknown>).timer,
        });
    });

});