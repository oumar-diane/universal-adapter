import {IVisibleFlows} from "@/core/flows-visibility.ts";


export const initVisibleFlows = (flowsIds: string[]) => {
    return flowsIds.reduce((flows, id, index) => {
        flows[id] = index === 0;
        return flows;
    }, {} as IVisibleFlows);
};
