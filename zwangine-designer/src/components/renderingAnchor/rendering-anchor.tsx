import { IVisualizationNode } from '@/core';
import { isDefined } from '@/lib/utility';
import { FunctionComponent, useContext } from 'react';
import {RenderingAnchorContext} from "@/components/renderingAnchor";

interface IRenderingAnchor {
    anchorTag: string;
    vizNode: IVisualizationNode | undefined;
}

export const RenderingAnchor: FunctionComponent<IRenderingAnchor> = ({ anchorTag, vizNode }) => {
    const renderingAnchorContext = useContext(RenderingAnchorContext);

    if (!isDefined(vizNode)) {
        return null;
    }

    const registeredChildren = renderingAnchorContext.getRegisteredComponents(anchorTag, vizNode);

    return registeredChildren.map(({ key, Component }) => <Component key={key} vizNode={vizNode} />);
};
