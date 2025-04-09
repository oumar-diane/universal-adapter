import { useVisualizationController } from '@patternfly/react-topology';
import { useCallback, useContext, useMemo } from 'react';
import {EntitiesContext} from "@/providers";
import {getVisualizationNodesFromGraph, setValue} from "@/lib/utility";

export const useEnableAllSteps = () => {
    const entitiesContext = useContext(EntitiesContext);
    const controller = useVisualizationController();
    const disabledNodes = useMemo(() => {
        return getVisualizationNodesFromGraph(controller.getGraph(), (node) => {
            return node.getComponentSchema()?.definition?.disabled;
        });
    }, [controller]);
    const areMultipleStepsDisabled = disabledNodes.length > 1;

    const onEnableAllSteps = useCallback(() => {
        disabledNodes.forEach((node) => {
            const newModel = node.getComponentSchema()?.definition || {};
            setValue(newModel, 'disabled', false);
            node.updateModel(newModel);
        });

        entitiesContext?.updateEntitiesFromResource();
    }, [disabledNodes, entitiesContext]);

    const value = useMemo(
        () => ({
            onEnableAllSteps,
            areMultipleStepsDisabled,
        }),
        [areMultipleStepsDisabled, onEnableAllSteps],
    );

    return value;
};
