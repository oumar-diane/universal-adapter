import {AddStepMode, IVisualizationNode} from '@/core/model/entity/base-visual-entity';
import {CatalogModalContext, EntitiesContext} from '@/providers';
import { useCallback, useContext, useMemo } from 'react';

export const useInsertStep = (
    vizNode: IVisualizationNode,
    mode: AddStepMode.InsertChildStep | AddStepMode.InsertSpecialChildStep = AddStepMode.InsertChildStep,
) => {
    const entitiesContext = useContext(EntitiesContext);
    const catalogModalContext = useContext(CatalogModalContext);

    const onInsertStep = useCallback(async () => {
        if (!vizNode || !entitiesContext) return;

        /** Get compatible nodes and the location where can be introduced */
        const compatibleNodes = entitiesContext.resource.getCompatibleComponents(
            mode,
            vizNode.data,
            vizNode.getComponentSchema()?.definition,
        );

        /** Open Catalog modal, filtering the compatible nodes */
        const definedComponent = await catalogModalContext?.getNewComponent(compatibleNodes);
        if (!definedComponent) return;
        const targetProperty = mode === AddStepMode.InsertChildStep ? 'steps' : undefined;

        /** Add new node to the entities */
        vizNode.addBaseEntityStep(definedComponent, mode, targetProperty);

        /** Update entity */
        entitiesContext.updateEntitiesFromResource();
    }, [catalogModalContext, entitiesContext, mode, vizNode]);

    const value = useMemo(
        () => ({
            onInsertStep,
        }),
        [onInsertStep],
    );

    return value;
};
