import { Menu, MenuContainer, MenuContent, MenuItem, MenuList, MenuToggle } from '@patternfly/react-core';
import { PlusIcon } from '@patternfly/react-icons';
import { FunctionComponent, ReactElement, useCallback, useContext, useRef, useState } from 'react';
import './new-entity.scss';
import { EntitiesContext, VisibleFlowsContext } from '@/providers';
import {BaseVisualEntityDefinition} from "@/core/model/resource/resource.ts";
import {EntityType} from "@/core/model/entity/base-entity.ts";

export const NewEntity: FunctionComponent = () => {
    const { resource, updateEntitiesFromResource } = useContext(EntitiesContext)!;
    const visibleFlowsContext = useContext(VisibleFlowsContext)!;
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);
    const toggleRef = useRef<HTMLButtonElement>(null);
    const groupedEntities = useRef<BaseVisualEntityDefinition>(resource.getCanvasEntityList());

    const onSelect = useCallback(
        (_event: unknown, entityType: string | number | undefined) => {
            if (!entityType) {
                return;
            }

            /**
             * If it's the same DSL as we have in the existing Flows list,
             * we don't need to do anything special, just add a new flow if
             * supported
             */
            const newId = resource.addNewEntity(entityType as EntityType);
            visibleFlowsContext.visualFlowsApi.hideAllFlows();
            visibleFlowsContext.visualFlowsApi.toggleFlowVisible(newId);
            updateEntitiesFromResource();
            setIsOpen(false);
        },
        [resource, updateEntitiesFromResource, visibleFlowsContext.visualFlowsApi],
    );

    const getMenuItem = useCallback(
        (
            entity:
                | { title: string; description?: string; name: EntityType }
                | { title: string; description?: string; key: string },
            flyoutMenu?: ReactElement,
        ) => {
            const name = 'name' in entity ? entity.name : entity.key;
            return (
                <MenuItem
                    key={`new-entity-${name}`}
                    data-testid={`new-entity-${name}`}
                    itemId={name}
                    description={
                        <span className="pf-v6-u-text-break-word" style={{ wordBreak: 'keep-all' }}>
              {entity.description}
            </span>
                    }
                    flyoutMenu={flyoutMenu}
                >
                    {entity.title}
                </MenuItem>
            );
        },
        [],
    );

    return (
        <MenuContainer
            isOpen={isOpen}
            onOpenChange={(isOpen) => setIsOpen(isOpen)}
            menu={
                <Menu ref={menuRef} containsFlyout onSelect={onSelect}>
                    <MenuContent>
                        <MenuList>
                            {groupedEntities.current.common.map((entityDef) => getMenuItem(entityDef))}

                            {Object.entries(groupedEntities.current.groups).map(([group, entities]) => {
                                const flyoutMenu = (
                                    <Menu className="entities-menu__submenu" onSelect={onSelect}>
                                        <MenuContent>
                                            <MenuList>{entities.map((entityDef) => getMenuItem(entityDef))}</MenuList>
                                        </MenuContent>
                                    </Menu>
                                );

                                return getMenuItem({ key: group, title: group }, flyoutMenu);
                            })}
                        </MenuList>
                    </MenuContent>
                </Menu>
            }
            menuRef={menuRef}
            toggle={
                <MenuToggle
                    className={"h-[40px]"}
                    data-testid="new-entity-list-dropdown"
                    ref={toggleRef}
                    onClick={() => {
                        setIsOpen(!isOpen);
                    }}
                    isExpanded={isOpen}
                >
                    <div className={"flex flex-row items-center space-x-1"}>
                        <PlusIcon />
                        <span className="pf-v6-u-m-sm">New</span>
                    </div>
                </MenuToggle>
            }
            toggleRef={toggleRef}
        />
    );
};
