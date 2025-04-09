import { useCallback, useContext, useMemo } from 'react';
import {EntitiesContext} from "@/providers";
import {setValue} from "@/lib/utility";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

export const useDisableStep = (vizNode: IVisualizationNode) => {
    const entitiesContext = useContext(EntitiesContext);
    const isDisabled = !!vizNode.getComponentSchema()?.definition?.disabled;

    const onToggleDisableNode = useCallback(() => {
        const newModel = vizNode.getComponentSchema()?.definition || {};
        setValue(newModel, 'disabled', !isDisabled);
        vizNode.updateModel(newModel);

        entitiesContext?.updateEntitiesFromResource();
    }, [entitiesContext, isDisabled, vizNode]);

    const value = useMemo(
        () => ({
            onToggleDisableNode,
            isDisabled,
        }),
        [isDisabled, onToggleDisableNode],
    );

    return value;
};
