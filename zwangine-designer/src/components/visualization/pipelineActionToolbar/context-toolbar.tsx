import { Toolbar, ToolbarContent, ToolbarItem } from '@patternfly/react-core';
import { FunctionComponent, JSX, useContext } from 'react';
import './context-toolbar.scss';
import {sourceSchemaConfig} from "@/core";
import {EntitiesContext} from "@/providers";
import {FlowsMenu, NewEntity} from "@/components/visualization";

export const ContextToolbar: FunctionComponent<{ additionalControls?: JSX.Element[] }> = ({ additionalControls }) => {
    const { currentSchemaType } = useContext(EntitiesContext)!;
    const isMultipleRoutes = sourceSchemaConfig.config[currentSchemaType].multipleWorkflow;

    const toolbarItems: JSX.Element[] = [
        <ToolbarItem key="toolbar-flows-list">
            <FlowsMenu />
        </ToolbarItem>,
    ];

    if (isMultipleRoutes) {
        toolbarItems.push(
            <ToolbarItem key="toolbar-new-route">
                <NewEntity />
            </ToolbarItem>,
        );
    }
    //Currently adding only SerializerSelector at the beginning of the toolbar,
    if (additionalControls) {
        additionalControls.forEach((control) => toolbarItems.unshift(control));
    }

    return (
        <Toolbar className={"p-0 m-0"}>
            <ToolbarContent className={"flex flex-row items-center justify-between h-[40px] px-2  "}>
                {toolbarItems}
            </ToolbarContent>
        </Toolbar>
    );
};
