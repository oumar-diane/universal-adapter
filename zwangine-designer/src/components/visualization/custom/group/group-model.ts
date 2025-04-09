import type { ElementModel, GraphElement, DefaultGroup as TopologyDefaultGroup } from '@patternfly/react-topology';
import {CanvasNode} from "@/components/visualization";

type DefaultGroupProps = Parameters<typeof TopologyDefaultGroup>[0];
export interface CustomGroupProps extends DefaultGroupProps {
    element: GraphElement<ElementModel, CanvasNode['data']>;
    /** Toggle node collapse / expand */
    onCollapseToggle?: () => void;
}
