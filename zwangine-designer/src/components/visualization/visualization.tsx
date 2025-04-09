import { FunctionComponent, PropsWithChildren, ReactNode } from 'react';
import './visualization.scss';
import { CanvasFormTabsProvider } from '@/providers';
import {Canvas, CanvasFallback} from "@/components/visualization";
import { ErrorBoundary } from '../error';
import {BaseVisualEntity} from "@/core/model/entity/base-visual-entity.ts";

interface CanvasProps {
    className?: string;
    entities: BaseVisualEntity[];
    fallback?: ReactNode;
}

export const Visualization: FunctionComponent<PropsWithChildren<CanvasProps>> = (props) => {
    return (
        <div className={`canvas-surface ${props.className ?? ''}`}>
            <CanvasFormTabsProvider>
                <ErrorBoundary fallback={props.fallback ?? <CanvasFallback />}>
                    <Canvas
                        entities={props.entities}
                    />
                </ErrorBoundary>
            </CanvasFormTabsProvider>
        </div>
    );
};
