import { getRandomId } from '@/lib/ua-utils';
import { createContext, FunctionComponent, PropsWithChildren, Suspense, useCallback, useMemo, useRef } from 'react';
import {IRegisteredComponent, IRenderingAnchorContext} from './rendering-provider-model';
import {Loading} from "@/components/loading";
import {IVisualizationNode} from "@/core/model/entity/base-visual-entity.ts";

// eslint-disable-next-line react-refresh/only-export-components
export const RenderingAnchorContext = createContext<IRenderingAnchorContext>({
    registerComponent: () => {},
    getRegisteredComponents: () => [],
});

export const RenderingProvider: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const registeredComponents = useRef<({ key: string } & IRegisteredComponent)[]>([]);

    const registerComponent = useCallback((props: IRegisteredComponent) => {
        const key = getRandomId(props.anchor, 6);
        registeredComponents.current.push({ key, ...props });
    }, []);

    const getRegisteredComponents = useCallback((anchorTag: string, vizNode: IVisualizationNode) => {
        return registeredComponents.current
            .filter(
                (registeredComponent) => registeredComponent.anchor === anchorTag && registeredComponent.activationFn(vizNode),
            )
            .map(({ key, component }) => ({ key, Component: component }));
    }, []);

    const value = useMemo(
        () => ({ registerComponent, getRegisteredComponents }),
        [getRegisteredComponents, registerComponent],
    );

    return (
        <Suspense
            fallback={
                <Loading>
                    <span>Loading dynamic components...</span>
                </Loading>
            }
        >
            <RenderingAnchorContext.Provider value={value}>{children}</RenderingAnchorContext.Provider>
        </Suspense>
    );
};
