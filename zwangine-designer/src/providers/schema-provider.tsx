import { Content, ContentVariants } from '@patternfly/react-core';
import { FunctionComponent, PropsWithChildren, createContext, useEffect, useState } from 'react';
import {LoadingStatus, CatalogIndex, SchemaDefinition, sourceSchemaConfig} from '@/core';
import { CatalogSchemaLoader } from '@/lib/utility';
import { Loading } from '@/components/loading';
import {LoadDefaultCatalog} from "@/components/loadDefaultCatalog";
import {useSchemasStore} from "@/lib/store";


// eslint-disable-next-line react-refresh/only-export-components
export const SchemasContext = createContext<Record<string, SchemaDefinition>>({});

/**
 * Loader for the components schemas.
 */

export const SchemasLoaderProvider: FunctionComponent<PropsWithChildren> = (props) => {
    const [loadingStatus, setLoadingStatus] = useState(LoadingStatus.Loading);
    const [errorMessage, setErrorMessage] = useState('');
    const setSchema = useSchemasStore((state) => state.setSchema);
    const [schemas, setSchemas] = useState<Record<string, SchemaDefinition>>({});

    useEffect(() => {
        fetch(CatalogSchemaLoader.DEFAULT_CATALOG_PATH+'/catalog-index.json')
            .then((response) => {
                setLoadingStatus(LoadingStatus.Loading);
                return response;
            })
            .then((response) => response.json())
            .then(async (catalogIndex: CatalogIndex) => {
                const schemaFilesPromise = CatalogSchemaLoader.getSchemasFiles(catalogIndex.schemas);

                const loadedSchemas = await Promise.all(schemaFilesPromise);
                const combinedSchemas = loadedSchemas.reduce(
                    (acc, schema) => {
                        setSchema(schema.name, schema);
                        sourceSchemaConfig.setSchema(schema.name, schema);
                        acc[schema.name] = schema;

                        return acc;
                    },
                    {} as Record<string, SchemaDefinition>,
                );

                setSchemas(combinedSchemas);
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
        <SchemasContext.Provider value={schemas}>
            {loadingStatus === LoadingStatus.Loading && (
                <Loading>
                    <Content data-testid="loading-schemas" component={ContentVariants.h3}>
                        Loading Schemas...
                    </Content>
                </Loading>
            )}

            {loadingStatus === LoadingStatus.Error && (
                <LoadDefaultCatalog errorMessage={errorMessage}>
                    Some schema files might not be available.
                    <br />
                    Please try to reload the page or load the default Catalog.
                </LoadDefaultCatalog>
            )}

            {loadingStatus === LoadingStatus.Loaded && props.children}
        </SchemasContext.Provider>
    );
};
