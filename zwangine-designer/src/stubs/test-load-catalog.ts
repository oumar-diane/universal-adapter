import { CatalogIndex } from "@/core";
import { IComponentDefinition } from "@/core/model/catalog/components-catalog";
import { IProcessorDefinition } from "@/core/model/catalog/processors-catalog";
import {ILanguageDefinition} from "@/core/model/catalog/languages-catalog.ts";
import {IDataformatDefinition} from "@/core/model/catalog/dataformats-catalog.ts";
import {ILoadBalancerDefinition} from "@/core/model/catalog/loadbalancers-catalog.ts";


export const getCatalogMap = async (catalogDefinition: CatalogIndex) => {
    return await testLoadCatalog(catalogDefinition as CatalogIndex);
};
export const testLoadCatalog = async (catalogDefinition: CatalogIndex) => {

    const catalogPath = `../assets/resources/`;

    const componentCatalogMap: Record<string, IComponentDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.components.file}`
        );
    delete componentCatalogMap.default;

    const modelCatalogMap: Record<string, IProcessorDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.models.file}`
        );
    delete modelCatalogMap.default;

    const patternCatalogMap: Record<string, IProcessorDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.patterns.file}`
        );
    delete patternCatalogMap.default;

    const languageCatalog: Record<string, ILanguageDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.languages.file}`
        );
    delete languageCatalog.default;

    const dataformatCatalog: Record<string, IDataformatDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.dataformats.file}`
        );
    delete dataformatCatalog.default;

    const loadbalancerCatalog: Record<string, ILoadBalancerDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.loadbalancers.file}`
        );
    delete loadbalancerCatalog.default;

    const entitiesCatalog: Record<string, IProcessorDefinition> = await import(
        `${catalogPath}${catalogDefinition.catalogs.entities.file}`
        );
    delete entitiesCatalog.default;

    return {
        catalogDefinition,
        catalogPath,
        componentCatalogMap,
        modelCatalogMap,
        patternCatalogMap,
        languageCatalog,
        dataformatCatalog,
        loadbalancerCatalog,
        entitiesCatalog,
    };
};
