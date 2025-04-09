import { Button, Icon } from '@patternfly/react-core';
import { EyeIcon, EyeSlashIcon, TrashIcon } from '@patternfly/react-icons';
import { Table, Tbody, Td, Th, Thead, Tr } from '@patternfly/react-table';
import { FunctionComponent, MouseEvent, useCallback, useContext, useRef } from 'react';
import { WorkflowIdValidator } from '@/components/inlineEdit';
import './flows-list.scss';
import {ACTION_ID_CONFIRM, ActionConfirmationModalContext, EntitiesContext, VisibleFlowsContext} from "@/providers";
import {InlineEdit} from "@/components/inlineEdit";
import {FlowsListEmptyState} from "@/components/visualization";
import {BaseVisualEntity} from "@/core/model/entity/base-visual-entity.ts";
import {ValidationResult} from "@/core/model/validation.ts";

interface IFlowsList {
    onClose?: () => void;
}

export const FlowsList: FunctionComponent<IFlowsList> = (props) => {
    const { visualEntities, resource, updateEntitiesFromResource } = useContext(EntitiesContext)!;
    const { visibleFlows, allFlowsVisible, visualFlowsApi } = useContext(VisibleFlowsContext)!;
    const deleteModalContext = useContext(ActionConfirmationModalContext);

    const isListEmpty = visualEntities.length === 0;

    const columnNames = useRef({
        id: 'Workflow Id',
        isVisible: 'Visibility',
        delete: 'Delete',
    });

    const onSelectFlow = useCallback(
        (flowId: string): void => {
            visualFlowsApi.hideAllFlows();
            visualFlowsApi.toggleFlowVisible(flowId);
            props.onClose?.();
        },
        [props, visualFlowsApi],
    );

    const workflowIdValidator = useCallback(
        (value: string): ValidationResult => {
            return WorkflowIdValidator.validateUniqueName(value, visualEntities);
        },
        [visualEntities],
    );

    const onToggleAll = useCallback(
        (event: MouseEvent<HTMLButtonElement>) => {
            if (allFlowsVisible) {
                visualFlowsApi.hideAllFlows();
            } else {
                visualFlowsApi.showAllFlows();
            }
            event.stopPropagation();
        },
        [allFlowsVisible, visualFlowsApi],
    );

    return isListEmpty ? (
        <FlowsListEmptyState data-testid="flows-list-empty-state" />
    ) : (
        <Table className="flows-list-table" variant="compact" data-testid="flows-list-table">
            <Thead noWrap>
                <Tr>
                    <Th>{columnNames.current.id}</Th>
                    <Th>
                        <Button
                            title={allFlowsVisible ? 'Hide all flows' : 'Show all flows'}
                            data-testid="toggle-btn-all-flows"
                            onClick={onToggleAll}
                            icon={
                                <Icon isInline>
                                    {allFlowsVisible ? (
                                        <EyeIcon data-testid="toggle-btn-hide-all" />
                                    ) : (
                                        <EyeSlashIcon data-testid="toggle-btn-show-all" />
                                    )}
                                </Icon>
                            }
                            variant="plain"
                        />
                    </Th>
                    <Th>{columnNames.current.delete}</Th>
                </Tr>
            </Thead>
            <Tbody>
                {visualEntities.map((flow: BaseVisualEntity) => (
                    <Tr key={flow.id} data-testid={`flows-list-row-${flow.id}`}>
                        <Td dataLabel={columnNames.current.id}>
                            <InlineEdit
                                editTitle={`Rename ${flow.id}`}
                                textTitle={`Focus on ${flow.id}`}
                                data-testid={`goto-btn-${flow.id}`}
                                value={flow.id}
                                validator={workflowIdValidator}
                                onClick={() => {
                                    onSelectFlow(flow.id);
                                }}
                                onChange={(name) => {
                                    visualFlowsApi.renameFlow(flow.id, name);
                                    flow.setId(name);
                                    updateEntitiesFromResource();
                                }}
                            />
                            {/*TODO add description*/}
                        </Td>

                        <Td dataLabel={columnNames.current.isVisible}>
                            <Button
                                data-testid={`toggle-btn-${flow.id}`}
                                icon={
                                    visibleFlows[flow.id] ? (
                                        <Icon isInline>
                                            <EyeIcon title={`Hide ${flow.id}`} data-testid={`toggle-btn-${flow.id}-visible`} />
                                        </Icon>
                                    ) : (
                                        <Icon isInline>
                                            <EyeSlashIcon title={`Show ${flow.id}`} data-testid={`toggle-btn-${flow.id}-hidden`} />
                                        </Icon>
                                    )
                                }
                                variant="plain"
                                onClick={(event) => {
                                    visualFlowsApi.toggleFlowVisible(flow.id);
                                    /** Required to avoid closing the Dropdown after clicking in the icon */
                                    event.stopPropagation();
                                }}
                            />
                        </Td>

                        <Td dataLabel={columnNames.current.delete}>
                            <Button
                                title={`Delete ${flow.id}`}
                                data-testid={`delete-btn-${flow.id}`}
                                icon={<TrashIcon />}
                                variant="plain"
                                onClick={async (event) => {
                                    const isDeleteConfirmed = await deleteModalContext?.actionConfirmation({
                                        title:
                                            "Do you want to delete the '" +
                                            flow.toVizNode().getId() +
                                            "' " +
                                            flow.toVizNode().getNodeTitle() +
                                            '?',
                                        text: 'All steps will be lost.',
                                    });

                                    if (isDeleteConfirmed !== ACTION_ID_CONFIRM) return;

                                    resource.removeEntity(flow.id);
                                    updateEntitiesFromResource();
                                    /** Required to avoid closing the Dropdown after clicking in the icon */
                                    event.stopPropagation();
                                }}
                            />
                        </Td>
                    </Tr>
                ))}
            </Tbody>
        </Table>
    );
};
