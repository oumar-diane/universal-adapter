import { Button } from '@patternfly/react-core';
import { Modal, ModalVariant } from '@patternfly/react-core/deprecated';
import { PlusIcon } from '@patternfly/react-icons';
import { FunctionComponent, PropsWithChildren, useCallback, useContext, useState } from 'react';
import {SourceCodeApiContext, VisibleFlowsContext} from "@/providers";
import {ISourceSchema, sourceSchemaConfig, SourceSchemaType} from "@/core";
import {useEntityContext} from "@/hooks";
import {FlowTypeSelector} from "@/components/visualization";
import {FlowTemplatesService} from "@/core/flow-templates-service.ts";

export const NewFlow: FunctionComponent<PropsWithChildren> = () => {
    const sourceCodeContextApi = useContext(SourceCodeApiContext);
    const { currentSchemaType, resource, updateEntitiesFromResource } = useEntityContext();
    const currentFlowType: ISourceSchema = sourceSchemaConfig.config[currentSchemaType];
    const visibleFlowsContext = useContext(VisibleFlowsContext)!;
    const [isConfirmationModalOpen, setIsConfirmationModalOpen] = useState(false);
    const [proposedFlowType, setProposedFlowType] = useState<SourceSchemaType>();

    const checkBeforeAddNewFlow = useCallback(
        (flowType: SourceSchemaType) => {
            const isSameSourceType = currentSchemaType === flowType;

            if (isSameSourceType) {
                /**
                 * If it's the same DSL as we have in the existing Flows list,
                 * we don't need to do anything special, just add a new flow if
                 * supported
                 */
                const newId = resource.addNewEntity();
                visibleFlowsContext.visualFlowsApi.hideAllFlows();
                visibleFlowsContext.visualFlowsApi.toggleFlowVisible(newId);
                updateEntitiesFromResource();
            } else {
                /**
                 * If it is not the same DSL, this operation might result in
                 * removing the existing flows, so then we warn the user first
                 */
                setProposedFlowType(flowType);
                setIsConfirmationModalOpen(true);
            }
        },
        [resource, currentSchemaType, updateEntitiesFromResource, visibleFlowsContext.visualFlowsApi],
    );

    return (
        <>
            <FlowTypeSelector isStatic onSelect={checkBeforeAddNewFlow}>
                <div
                    title={
                        currentFlowType.multipleWorkflow
                            ? `Add a new ${currentFlowType.name} workflow`
                            : `The ${currentFlowType.name} type does not support multiple workflows`
                    }
                >
                    <PlusIcon />
                    <span className="pf-v6-u-m-sm">New</span>
                </div>
            </FlowTypeSelector>
            <Modal
                variant={ModalVariant.small}
                title="Warning"
                data-testid="confirmation-modal"
                titleIconVariant="warning"
                onClose={() => {
                    setIsConfirmationModalOpen(false);
                }}
                actions={[
                    <Button
                        key="confirm"
                        variant="primary"
                        data-testid="confirmation-modal-confirm"
                        onClick={() => {
                            if (proposedFlowType) {
                                sourceCodeContextApi.setCodeAndNotify(FlowTemplatesService.getFlowYamlTemplate(proposedFlowType));
                                setIsConfirmationModalOpen(false);
                            }
                        }}
                    >
                        Confirm
                    </Button>,
                    <Button
                        key="cancel"
                        variant="link"
                        data-testid="confirmation-modal-cancel"
                        onClick={() => {
                            setIsConfirmationModalOpen(false);
                        }}
                    >
                        Cancel
                    </Button>,
                ]}
                isOpen={isConfirmationModalOpen}
            >
                <p>
                    This will remove any existing integration and you will lose your current work. Are you sure you would like to
                    proceed?
                </p>
            </Modal>
        </>
    );
};
