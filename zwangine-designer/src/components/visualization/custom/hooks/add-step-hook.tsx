import {AddStepMode, IVisualizationNode} from '@/core/model/entity/base-visual-entity';
import {CatalogModalContext, EntitiesContext} from '@/providers';
import { useCallback, useContext, useMemo } from 'react';

export const useAddStep = (
    vizNode: IVisualizationNode,
    mode: AddStepMode.PrependStep | AddStepMode.AppendStep = AddStepMode.AppendStep,
) => {
    const entitiesContext = useContext(EntitiesContext);
    const catalogModalContext = useContext(CatalogModalContext);

    const onAddStep = useCallback(async () => {
        if (!vizNode || !entitiesContext) return;

        /** Get compatible nodes and the location where can be introduced */
        const compatibleNodes = entitiesContext.resource.getCompatibleComponents(mode, vizNode.data);

        /** Open Catalog modal, filtering the compatible nodes */
        const definedComponent = await catalogModalContext?.getNewComponent(compatibleNodes);
        if (!definedComponent) return;

        /** Add new node to the entities */
        vizNode.addBaseEntityStep(definedComponent, mode);

        /** Update entity */
        entitiesContext.updateEntitiesFromResource();

    }, [catalogModalContext, entitiesContext, mode, vizNode]);

    const value = useMemo(
        () => ({
            onAddStep,
        }),
        [onAddStep],
    );

    return value;
};
