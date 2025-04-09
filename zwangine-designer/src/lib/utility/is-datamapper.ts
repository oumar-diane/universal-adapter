import {ProcessorDefinition, Step} from "@/@types";


export const DATAMAPPER_ID_PREFIX = 'datamapper' as keyof ProcessorDefinition;
export const XSLT_COMPONENT_NAME = 'xslt-saxon';

export const isDataMapperNode = (stepDefinition: Step): stepDefinition is Step => {
    const isDatamapperId = stepDefinition.id?.startsWith(DATAMAPPER_ID_PREFIX) ?? false;
    const doesContainXslt =
        stepDefinition.steps?.some((step) => {
            if (typeof step.to === 'string') {
                return step.to.startsWith(XSLT_COMPONENT_NAME);
            }

            return step.to?.uri?.startsWith(XSLT_COMPONENT_NAME);
        }) ?? false;

    return isDatamapperId && doesContainXslt;
};
