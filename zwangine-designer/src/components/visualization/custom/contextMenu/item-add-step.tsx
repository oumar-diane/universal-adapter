import {IDataTestID} from '@/core';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {useAddStep} from "@/components/visualization";
import {AddStepMode, IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemAddStepProps extends PropsWithChildren<IDataTestID> {
    mode: AddStepMode.PrependStep | AddStepMode.AppendStep;
    vizNode: IVisualizationNode;
}

export const ItemAddStep: FunctionComponent<ItemAddStepProps> = (props) => {
    const { onAddStep } = useAddStep(props.vizNode, props.mode);

    return (
        <ContextMenuItem onClick={onAddStep} data-testid={props['data-testid']}>
            {props.children}
        </ContextMenuItem>
    );
};
