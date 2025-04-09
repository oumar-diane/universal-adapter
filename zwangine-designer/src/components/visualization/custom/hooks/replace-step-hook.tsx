import {IInteractionAddonType, NodeInteractionAddonContext } from '@/components/registers';
import {ACTION_ID_CANCEL, ACTION_ID_CONFIRM, ActionConfirmationModalContext, CatalogModalContext, EntitiesContext } from '@/providers';
import { useCallback, useContext, useMemo } from 'react';
import {
    findModalCustomizationRecursively,
    processNodeInteractionAddonRecursively
} from '../contextMenu/item-delete-helper';
import {AddStepMode, IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

export const useReplaceStep = (vizNode: IVisualizationNode) => {
    const entitiesContext = useContext(EntitiesContext);
    const catalogModalContext = useContext(CatalogModalContext);
    const replaceModalContext = useContext(ActionConfirmationModalContext);
    const childrenNodes = vizNode.getChildren();
    const hasChildren = childrenNodes !== undefined && childrenNodes.length > 0;
    const isPlaceholderNode = hasChildren && childrenNodes.length === 1 && childrenNodes[0].data.isPlaceholder;
    const { getRegisteredInteractionAddons } = useContext(NodeInteractionAddonContext);

    const onReplaceNode = useCallback(async () => {

        if (!vizNode || !entitiesContext) return;

        const modalCustoms = findModalCustomizationRecursively(vizNode, (vn) =>
            getRegisteredInteractionAddons(IInteractionAddonType.ON_DELETE, vn),
        );
        let modalAnswer: string | undefined = ACTION_ID_CONFIRM;
        if (hasChildren && !isPlaceholderNode) {
            const additionalModalText = modalCustoms.length > 0 ? modalCustoms[0].additionalText : undefined;
            const buttonOptions = modalCustoms.length > 0 ? modalCustoms[0].buttonOptions : undefined;
            /** Open delete confirm modal, get the confirmation  */
            modalAnswer = await replaceModalContext?.actionConfirmation({
                title: 'Replace step?',
                text: 'Step and its children will be lost.',
                additionalModalText,
                buttonOptions,
            });

            if (!modalAnswer || modalAnswer === ACTION_ID_CANCEL) return;
        }

        /** Find compatible components */
        const catalogFilter = entitiesContext.resource.getCompatibleComponents(AddStepMode.ReplaceStep, vizNode.data);

        /** Open Catalog modal, filtering the compatible nodes */
        const definedComponent = await catalogModalContext?.getNewComponent(catalogFilter);
        if (!definedComponent) return;

        processNodeInteractionAddonRecursively(vizNode, modalAnswer, (vn) =>
            getRegisteredInteractionAddons(IInteractionAddonType.ON_DELETE, vn),
        );

        /** Add new node to the entities */
        vizNode.addBaseEntityStep(definedComponent, AddStepMode.ReplaceStep);

        /** Update entity */
        entitiesContext.updateEntitiesFromResource();
    }, [catalogModalContext, entitiesContext, getRegisteredInteractionAddons, hasChildren, replaceModalContext, vizNode]);

    const value = useMemo(
        () => ({
            onReplaceNode,
        }),
        [onReplaceNode],
    );

    return value;
};
