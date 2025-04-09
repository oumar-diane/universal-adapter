import { PowerOffIcon } from '@patternfly/react-icons';
import { ContextMenuItem } from '@patternfly/react-topology';
import { FunctionComponent, PropsWithChildren } from 'react';
import {IDataTestID} from "@/core";
import {useEnableAllSteps} from "@/components/visualization";

export const ItemEnableAllSteps: FunctionComponent<PropsWithChildren<IDataTestID>> = (props) => {
    const { areMultipleStepsDisabled, onEnableAllSteps } = useEnableAllSteps();
    if (!areMultipleStepsDisabled) {
        return null;
    }

    return (
        <ContextMenuItem onClick={onEnableAllSteps} data-testid={props['data-testid']}>
            <PowerOffIcon /> Enable All
        </ContextMenuItem>
    );
};
