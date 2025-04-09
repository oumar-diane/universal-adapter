import { TrashIcon } from '@patternfly/react-icons';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {IDataTestID} from "@/core";
import {useDeleteStep} from "@/components/visualization";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemDeleteStepProps extends PropsWithChildren<IDataTestID> {
    vizNode: IVisualizationNode;
}

export const ItemDeleteStep: FunctionComponent<ItemDeleteStepProps> = (props) => {
    const { onDeleteStep } = useDeleteStep(props.vizNode);

    return (
        <ContextMenuItem onClick={onDeleteStep} data-testid={props['data-testid']}>
            <TrashIcon /> Delete
        </ContextMenuItem>
    );
};
