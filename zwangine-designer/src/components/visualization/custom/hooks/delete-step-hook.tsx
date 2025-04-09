import {ACTION_ID_CANCEL, ACTION_ID_CONFIRM, ActionConfirmationModalContext, EntitiesContext} from '@/providers';
import { useCallback, useContext, useMemo } from 'react';
import {IInteractionAddonType, NodeInteractionAddonContext} from "@/components/registers";
import { findModalCustomizationRecursively, processNodeInteractionAddonRecursively } from '@/components/visualization';
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

export const useDeleteStep = (vizNode: IVisualizationNode) => {
    const entitiesContext = useContext(EntitiesContext);
    const deleteModalContext = useContext(ActionConfirmationModalContext);
    const childrenNodes = vizNode.getChildren();
    const hasChildren = childrenNodes !== undefined && childrenNodes.length > 0;
    const isPlaceholderNode = hasChildren && childrenNodes.length === 1 && childrenNodes[0].data.isPlaceholder;
    const { getRegisteredInteractionAddons } = useContext(NodeInteractionAddonContext);

    const onDeleteStep = useCallback(async () => {
        const modalCustoms = findModalCustomizationRecursively(vizNode, (vn) =>
            getRegisteredInteractionAddons(IInteractionAddonType.ON_DELETE, vn),
        );

        let modalAnswer: string | undefined = ACTION_ID_CONFIRM;
        if ((hasChildren && !isPlaceholderNode) || modalCustoms.length > 0) {
            const additionalModalText = modalCustoms.length > 0 ? modalCustoms[0].additionalText : undefined;
            const buttonOptions = modalCustoms.length > 0 ? modalCustoms[0].buttonOptions : undefined;
            /** Open delete confirm modal, get the confirmation  */
            modalAnswer = await deleteModalContext?.actionConfirmation({
                title: 'Permanently delete step?',
                text: 'Step and its children will be lost.',
                additionalModalText,
                buttonOptions,
            });

            if (!modalAnswer || modalAnswer === ACTION_ID_CANCEL) return;
        }

        processNodeInteractionAddonRecursively(vizNode, modalAnswer, (vn) =>
            getRegisteredInteractionAddons(IInteractionAddonType.ON_DELETE, vn),
        );

        vizNode.removeChild();
        entitiesContext?.updateEntitiesFromResource();
    }, [deleteModalContext, entitiesContext, getRegisteredInteractionAddons, hasChildren, vizNode]);

    const value = useMemo(
        () => ({
            onDeleteStep,
        }),
        [onDeleteStep],
    );

    return value;
};
