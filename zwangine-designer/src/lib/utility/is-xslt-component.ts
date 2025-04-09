import { XSLT_COMPONENT_NAME } from './is-datamapper.ts';
import type { ToObjectDef } from './is-to-processor.ts';
import { isToProcessor } from './is-to-processor.ts';
import {ProcessorDefinition} from "@/@types";

export type XsltComponentDef = ToObjectDef & { to: { uri: string } };

export const isXSLTComponent = (toDefinition: ProcessorDefinition): toDefinition is XsltComponentDef => {
    if (!isToProcessor(toDefinition)) {
        return false;
    }

    return toDefinition.to.uri?.startsWith(XSLT_COMPONENT_NAME) ?? false;
};
