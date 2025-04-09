import {IDataTestID} from '@/core';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {useInsertStep} from "@/components/visualization";
import {AddStepMode, IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemInsertStepProps extends PropsWithChildren<IDataTestID> {
    mode: AddStepMode.InsertChildStep | AddStepMode.InsertSpecialChildStep;
    vizNode: IVisualizationNode;
}

export const ItemInsertStep: FunctionComponent<ItemInsertStepProps> = (props) => {
    const { onInsertStep } = useInsertStep(props.vizNode, props.mode);

    return (
        <ContextMenuItem onClick={onInsertStep} data-testid={props['data-testid']}>
            {props.children}
        </ContextMenuItem>
    );
};
