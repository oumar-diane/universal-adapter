import { BanIcon, CheckIcon } from '@patternfly/react-icons';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {IDataTestID} from "@/core";
import {useDisableStep} from "@/components/visualization";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

interface ItemDisableStepProps extends PropsWithChildren<IDataTestID> {
    vizNode: IVisualizationNode;
}

export const ItemDisableStep: FunctionComponent<ItemDisableStepProps> = (props) => {
    const { onToggleDisableNode, isDisabled } = useDisableStep(props.vizNode);

    return (
        <ContextMenuItem onClick={onToggleDisableNode} data-testid={props['data-testid']}>
            {isDisabled ? (
                <>
                    <CheckIcon /> Enable
                </>
            ) : (
                <>
                    <BanIcon /> Disable
                </>
            )}
        </ContextMenuItem>
    );
};
