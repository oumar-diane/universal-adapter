import { Badge, Icon, MenuToggle, MenuToggleAction, MenuToggleElement, Select } from '@patternfly/react-core';
import { ListIcon } from '@patternfly/react-icons';
import { FunctionComponent, Ref, useCallback, useContext, useState } from 'react';

import './flows-list.scss';
import {VisibleFlowsContext} from "@/providers";
import {FlowsList} from "@/components/visualization";
import {getVisibleFlowsInformation} from "@/core/flows-visibility.ts";

export const FlowsMenu: FunctionComponent = () => {
    const { visibleFlows } = useContext(VisibleFlowsContext)!;
    const [isOpen, setIsOpen] = useState(false);
    const visibleFlowsInformation = useCallback(() => {
        return getVisibleFlowsInformation(visibleFlows);
    }, [visibleFlows]);

    const { singleFlowId, visibleFlowsCount, totalFlowsCount } = visibleFlowsInformation();

    /** Toggle the DSL dropdown */
    const onToggleClick = () => {
        setIsOpen(!isOpen);
    };

    const toggle = (toggleRef: Ref<MenuToggleElement>) => (
        <MenuToggle
            data-testid="flows-list-dropdown"
            className="h-[40px]"
            ref={toggleRef}
            onClick={onToggleClick}
            isFullWidth
            splitButtonItems={[
                <MenuToggleAction
                    id="flows-list-btn"
                    key="flows-list-btn"
                    data-testid="flows-list-btn"
                    aria-label="flows list"
                    onClick={onToggleClick}
                >
                    <div className="flows-menu">
                        <Icon isInline>
                            <ListIcon />
                        </Icon>
                        <span
                            title={singleFlowId ?? 'Routes'}
                            data-testid="flows-list-route-id"
                            className="pf-v6-u-m-sm flows-menu-display"
                        >
              {`${singleFlowId ?? 'Workflows'}`}
            </span>
                        <Badge
                            title={`Showing ${visibleFlowsCount} out of ${totalFlowsCount} flows`}
                            data-testid="flows-list-route-count"
                            isRead
                        >
                            {visibleFlowsCount}/{totalFlowsCount}
                        </Badge>
                    </div>
                </MenuToggleAction>,
            ]}
        />
    );

    return (
        <Select id="flows-list-select" isOpen={isOpen} onOpenChange={setIsOpen} toggle={toggle}>
            <FlowsList
                onClose={() => {
                    setIsOpen(false);
                }}
            />
        </Select>
    );
};
