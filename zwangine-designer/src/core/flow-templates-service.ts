import { parse } from 'yaml';
import {SourceSchemaType} from "./model/schema/schema.ts";
import {workflowTemplate} from "@/core/workflow-template.ts";

export class FlowTemplatesService {
    static getFlowTemplate = (type: SourceSchemaType) => {
        return parse(this.getFlowYamlTemplate(type));
    };

    static getFlowYamlTemplate = (type: SourceSchemaType): string => {
        switch (type) {
            case SourceSchemaType.Workflow:
                return workflowTemplate();
            default:
                return '';
        }
    };
}
