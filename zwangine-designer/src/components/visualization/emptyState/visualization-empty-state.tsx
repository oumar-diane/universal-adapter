import  {
    Bullseye,
    Card,
    EmptyState,
    EmptyStateActions,
    EmptyStateBody,
    EmptyStateFooter,
} from '@patternfly/react-core';
import { CubesIcon as PatternFlyCubesIcon, EyeSlashIcon as PatternFlyEyeSlashIcon } from '@patternfly/react-icons';
import { FunctionComponent, useMemo } from 'react';
import {IDataTestID} from "@/core";
import {NewFlow} from "@/components/visualization";

const CubesIcon: FunctionComponent = (props) => <PatternFlyCubesIcon data-testid="cubes-icon" {...props} />;
const EyeSlashIcon: FunctionComponent = (props) => <PatternFlyEyeSlashIcon data-testid="eye-slash-icon" {...props} />;

interface IVisualizationEmptyState extends IDataTestID {
    entitiesNumber: number;
    className?: string;
}

export const VisualizationEmptyState: FunctionComponent<IVisualizationEmptyState> = (props) => {
    const hasWorkflows = useMemo(() => props.entitiesNumber > 0, [props.entitiesNumber]);

    return (
        <Bullseye className={props.className}>
            <Card>
                <EmptyState
                    headingLevel="h4"
                    icon={hasWorkflows ? EyeSlashIcon : CubesIcon}
                    titleText={hasWorkflows ? <p>There are no visible workflows</p> : <p>There are no workflows defined</p>}
                    data-testid={props['data-testid']}
                >
                    <EmptyStateBody>
                        {hasWorkflows ? (
                            <p>You can toggle the visibility of a route by using Workflows list</p>
                        ) : (
                            <p>You can create a new workflow using the New button</p>
                        )}
                    </EmptyStateBody>
                    <EmptyStateFooter>
                        <EmptyStateActions>{!hasWorkflows && <NewFlow />}</EmptyStateActions>
                    </EmptyStateFooter>
                </EmptyState>
            </Card>
        </Bullseye>
    );
};
