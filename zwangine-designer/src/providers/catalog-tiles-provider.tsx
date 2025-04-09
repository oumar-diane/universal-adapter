import { FunctionComponent, PropsWithChildren, createContext, useContext, useMemo } from 'react';
import { ITile } from '@/components/catalog';
import { CatalogContext } from './catalog-provider';
import { CatalogKind } from '@/core';
import {isDefined} from "@/lib/utility";
import {componentToTile, entityToTile, processorToTile} from "@/lib/ua-utils"

// eslint-disable-next-line react-refresh/only-export-components
export const CatalogTilesContext = createContext<ITile[]>([]);

/**
 * The goal for this provider is to receive the Tiles in a single place once, and then supply them to the Catalog instances,
 * since this could be an expensive operation, and we don't want to do it for every Catalog instance
 */
export const CatalogTilesProvider: FunctionComponent<PropsWithChildren> = (props) => {
    const catalogService = useContext(CatalogContext);

    const tiles = useMemo(() => {
        const combinedTiles: ITile[] = [];

        Object.values(catalogService.getCatalogByKey(CatalogKind.Component) ?? {}).forEach((component) => {
            combinedTiles.push(componentToTile(component));
        });
        /**
         * To build the Patterns catalog, we use the short list, as opposed of the CatalogKind.Processor which have all definitions
         * This is because the short list contains only the patterns that can be used within an integration.
         *
         * The full list of patterns is available in the CatalogKind.Processor catalog and it's being used as lookup for components properties.
         */
        Object.values(catalogService.getCatalogByKey(CatalogKind.Pattern) ?? {}).forEach((processor) => {
            combinedTiles.push(processorToTile(processor));
        });
        Object.values(catalogService.getCatalogByKey(CatalogKind.Entity) ?? {}).forEach((entity) => {
            if (isDefined(entity.model)) {
                combinedTiles.push(entityToTile(entity));
            }
        });

        return combinedTiles;
    }, [catalogService]);

    return <CatalogTilesContext.Provider value={tiles}>{props.children}</CatalogTilesContext.Provider>;
};
