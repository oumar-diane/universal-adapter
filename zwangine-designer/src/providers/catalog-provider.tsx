import { Loading } from '@/components/loading';
import { CatalogSchemaLoader } from '@/lib/utility';
import { Content, ContentVariants } from '@patternfly/react-core';
import { FunctionComponent, PropsWithChildren, createContext, useEffect, useState } from 'react';
import {LoadDefaultCatalog} from "@/components/loadDefaultCatalog";
import { CatalogService, CatalogKind, CatalogIndex, ComponentsCatalog, LoadingStatus } from '@/core';


export const CatalogContext = createContext<typeof CatalogService>(CatalogService);

/**
 * Loader for the components catalog.
 */
export const CatalogLoaderProvider: FunctionComponent<PropsWithChildren> = (props) => {
    const [loadingStatus, setLoadingStatus] = useState(LoadingStatus.Loading);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const indexFile = `${CatalogSchemaLoader.DEFAULT_CATALOG_PATH}/catalog-index.json`;
        const relativeBasePath = CatalogSchemaLoader.getRelativeBasePath(indexFile);
        fetch(indexFile)
            .then((response) => {
                setLoadingStatus(LoadingStatus.Loading);
                return response;
            })
            .then((response) => response.json())
            .then(async (catalogIndex: CatalogIndex) => {
                /** Component list */
                const componentsFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Component]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.components.file}`,
                );
                /** Full list of Ua Models, used as lookup for processors definitions definitions */
                const modelsFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Processor]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.models.file}`,
                );
                /** Short list of patterns (EIPs) to fill the Catalog, as opposed of the CatalogKind.Processor which have all definitions */
                const patternsFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Pattern]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.patterns.file}`,
                );
                /** Short list of entities to fill the Catalog, as opposed of the CatalogKind.Processor which have all definitions */
                const entitiesFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Entity]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.entities.file}`,
                );
                /** Langges list */
                const langgesFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Language]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.languages.file}`,
                );
                /** Dataformats list */
                const dataformatsFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Dataformat]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.dataformats.file}`,
                );
                /** Loadbalancers list */
                const loadbalancersFiles = CatalogSchemaLoader.fetchFile<ComponentsCatalog[CatalogKind.Loadbalancer]>(
                    `${relativeBasePath}/${catalogIndex.catalogs.loadbalancers.file}`,
                );



                const [
                    components,
                    models,
                    patterns,
                    entities,
                    languages,
                    dataformats,
                    loadbalancers,
                ] = await Promise.all([
                    componentsFiles,
                    modelsFiles,
                    patternsFiles,
                    entitiesFiles,
                    langgesFiles,
                    dataformatsFiles,
                    loadbalancersFiles,
                ]);

                CatalogService.setCatalogKey(CatalogKind.Component, components.body);
                CatalogService.setCatalogKey(CatalogKind.Processor, {
                    ...models.body,
                });
                CatalogService.setCatalogKey(CatalogKind.Pattern, {
                    ...patterns.body,
                });
                CatalogService.setCatalogKey(CatalogKind.Entity, entities.body);
                CatalogService.setCatalogKey(CatalogKind.Language, languages.body);
                CatalogService.setCatalogKey(CatalogKind.Dataformat, dataformats.body);
                CatalogService.setCatalogKey(CatalogKind.Loadbalancer, loadbalancers.body);
            })
            .then(() => {
                setLoadingStatus(LoadingStatus.Loaded);
            })
            .catch((error) => {
                setErrorMessage(error.message);
                setLoadingStatus(LoadingStatus.Error);
            });
    }, []);

    return (
        <CatalogContext.Provider value={CatalogService}>
            {loadingStatus === LoadingStatus.Loading && (
                <Loading>
                    <Content data-testid="loading-catalogs" component={ContentVariants.h3}>
                        Loading Catalogs...
                    </Content>
                </Loading>
            )}

            {loadingStatus === LoadingStatus.Error && (
                <LoadDefaultCatalog errorMessage={errorMessage}>
                    Some catalog files might not be available.
                    <br />
                    Please try to reload the page or load the default Catalog.
                </LoadDefaultCatalog>
            )}

            {loadingStatus === LoadingStatus.Loaded && props.children}
        </CatalogContext.Provider>
    );
};
