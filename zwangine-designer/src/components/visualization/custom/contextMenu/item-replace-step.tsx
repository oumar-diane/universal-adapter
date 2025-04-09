import {IDataTestID} from '@/core';
import { SyncAltIcon } from '@patternfly/react-icons';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {useReplaceStep} from "@/components/visualization";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemReplaceStepProps extends PropsWithChildren<IDataTestID> {
    vizNode: IVisualizationNode;
    loadActionConfirmationModal: boolean;
}

export const ItemReplaceStep: FunctionComponent<ItemReplaceStepProps> = (props) => {
    const { onReplaceNode } = useReplaceStep(props.vizNode);

    return (
        <ContextMenuItem onClick={onReplaceNode}>
            <SyncAltIcon /> Replace
        </ContextMenuItem>
    );
};
