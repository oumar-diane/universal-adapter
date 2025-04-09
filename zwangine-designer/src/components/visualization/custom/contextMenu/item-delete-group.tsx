import {IDataTestID} from '@/core';
import { TrashIcon } from '@patternfly/react-icons';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {useDeleteGroup} from "@/components/visualization";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemDeleteGroupProps extends PropsWithChildren<IDataTestID> {
    vizNode: IVisualizationNode;
}

export const ItemDeleteGroup: FunctionComponent<ItemDeleteGroupProps> = (props) => {
    const { onDeleteGroup } = useDeleteGroup(props.vizNode);

    return (
        <ContextMenuItem onClick={onDeleteGroup} data-testid={props['data-testid']}>
            <TrashIcon /> Delete
        </ContextMenuItem>
    );
};
