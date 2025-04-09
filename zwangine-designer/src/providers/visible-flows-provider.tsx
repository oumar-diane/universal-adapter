import { FunctionComponent, PropsWithChildren, createContext, useContext, useEffect, useMemo, useReducer } from 'react';
import { EntitiesContext } from '@/providers';
import {initVisibleFlows} from "@/lib/utility";
import {IVisibleFlows, VisibleFlowsReducer, VisualFlowsApi} from "@/core/flows-visibility.ts";

export interface VisibleFlowsContextResult {
    visibleFlows: IVisibleFlows;
    allFlowsVisible: boolean;
    visualFlowsApi: VisualFlowsApi;
}

// eslint-disable-next-line react-refresh/only-export-components
export const VisibleFlowsContext = createContext<VisibleFlowsContextResult | undefined>(undefined);

export const VisibleFlowsProvider: FunctionComponent<PropsWithChildren> = (props) => {
    const entitiesContext = useContext(EntitiesContext);
    const visualEntitiesIds = useMemo(
        () => entitiesContext?.visualEntities.map((entity) => entity.id) ?? [],
        [entitiesContext?.visualEntities],
    );

    const [visibleFlows, dispatch] = useReducer(VisibleFlowsReducer, {}, () => initVisibleFlows(visualEntitiesIds));
    const allFlowsVisible = Object.values(visibleFlows).every((visible) => visible);
    const visualFlowsApi = useMemo(() => {
        return new VisualFlowsApi(dispatch);
    }, [dispatch]);

    useEffect(() => {
        visualFlowsApi.initVisibleFlows(visualEntitiesIds);
    }, [visualEntitiesIds, visualFlowsApi]);

    const value = useMemo(() => {
        return {
            visibleFlows,
            allFlowsVisible,
            visualFlowsApi,
        };
    }, [allFlowsVisible, visibleFlows, visualFlowsApi]);

    return <VisibleFlowsContext.Provider value={value}>{props.children}</VisibleFlowsContext.Provider>;
};
