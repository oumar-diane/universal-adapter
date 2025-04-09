import {SourceSchemaType} from '@/core';
import { BaseEntity } from '@/core/model/entity/base-entity';
import { BaseVisualEntity } from '@/core/model/entity/base-visual-entity';
import { Resource } from '@/core/model/resource/resource';
import { EventNotifier } from '@/lib/utility';
import {
    FunctionComponent,
    PropsWithChildren,
    createContext,
    useCallback,
    useLayoutEffect,
    useMemo,
    useState,
} from 'react';
import {ResourceFactory} from "@/core/resource/resource-factory.ts";

export interface EntitiesContextResult {
    entities: BaseEntity[];
    currentSchemaType: SourceSchemaType;
    visualEntities: BaseVisualEntity[];
    resource: Resource;

    /**
     * Notify that a property in an entity has changed, hence the source
     * code needs to be updated
     *
     * NOTE: This process shouldn't recreate the Resource neither
     * the entities, just the source code
     */
    updateSourceCodeFromEntities: () => void;

    /**
     * Refresh the entities from the Resource, and
     * notify subscribers that a `entities:updated` happened
     *
     * NOTE: This process shouldn't recreate the Resource,
     * just the entities
     */
    updateEntitiesFromResource: () => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const EntitiesContext = createContext<EntitiesContextResult | null>(null);

export const EntitiesProvider: FunctionComponent<PropsWithChildren<{ fileExtension?: string }>> = ({
                                                                                                       fileExtension,
                                                                                                       children,
                                                                                                   }) => {
    const eventNotifier = EventNotifier.getInstance();
    const [resource, setResource] = useState<Resource>(
        ResourceFactory.createResource('', { path: fileExtension }),
    );
    const [entities, setEntities] = useState<BaseEntity[]>([]);
    const [visualEntities, setVisualEntities] = useState<BaseVisualEntity[]>([]);

    /**
     * Subscribe to the `code:updated` event to recreate the Resource
     */
    useLayoutEffect(() => {
        return eventNotifier.subscribe('code:updated', ({ code, path }) => {
            const resource = ResourceFactory.createResource(code, { path });
            const entities = resource.getEntities();
            const visualEntities = resource.getVisualEntities();

            setResource(resource);
            setEntities(entities);
            setVisualEntities(visualEntities);
        });
    }, [eventNotifier]);

    const updateSourceCodeFromEntities = useCallback(() => {
        const code = resource.toString();
        eventNotifier.next('entities:updated', code);
    }, [resource, eventNotifier]);

    const updateEntitiesFromResource = useCallback(() => {
        const entities = resource.getEntities();
        const visualEntities = resource.getVisualEntities();
        setEntities(entities);
        setVisualEntities(visualEntities);

        /**
         * Notify consumers that entities has been refreshed, hence the code needs to be updated
         */
        updateSourceCodeFromEntities();
    }, [resource, updateSourceCodeFromEntities]);

    const value = useMemo(
        () => ({
            entities,
            visualEntities,
            currentSchemaType: resource?.getType(),
            resource,
            updateEntitiesFromResource,
            updateSourceCodeFromEntities,
        }),
        [entities, visualEntities, resource, updateEntitiesFromResource, updateSourceCodeFromEntities],
    );

    return <EntitiesContext.Provider value={value}>{children}</EntitiesContext.Provider>;
};
