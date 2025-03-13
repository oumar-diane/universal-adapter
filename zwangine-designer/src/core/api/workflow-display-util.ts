
import { Integration, WorkflowElement } from '../model/integration-defintion';
import { WorkflowUtil } from './workflow-util';
import { WorkflowDefinitionApi } from './workflow-definition-api';
import { WorkflowDefinitionApiExt } from './workflow-definition-api-ext';
import { RouteDefinition } from '../model/workflow-definition';
import { ComponentApi } from './component-api';
import {WorkflowMetadataApi} from "@/core/model/workflow-metadata.ts";

export class WorkflowDisplayUtil {
    private constructor() {}

    static getTitle = (element: WorkflowElement): string => {
        if (element.dslName === 'RouteDefinition') {
            const routeId = (element as RouteDefinition).id
            return routeId ? routeId : WorkflowUtil.capitalizeName((element as any).stepName);
        } else if ((element as any).uri && (['ToDefinition', 'FromDefinition', 'PollDefinition', 'ToDynamicDefinition'].includes(element.dslName))) {
            const uri = (element as any).uri
            return ComponentApi.getComponentTitleFromUri(uri) || '';
        } else {
            const title = WorkflowMetadataApi.getWorkflowModelMetadataByClassName(element.dslName);
            return title ? title.title : WorkflowUtil.capitalizeName((element as any).stepName);
        }
    }

    static getDescription = (element: WorkflowElement): string => {
        if ((element as any).uri && (['ToDefinition', 'FromDefinition', 'PollDefinition', 'ToDynamicDefinition'].includes(element.dslName))) {
            const uri = (element as any).uri
            return ComponentApi.getComponentDescriptionFromUri(uri) || '';
        } else {
            const description = WorkflowMetadataApi.getWorkflowModelMetadataByClassName(element.dslName)?.description;
            return description ? description : WorkflowDisplayUtil.getTitle(element);
        }
    }

    static getStepDescription = (element: WorkflowElement): string => {
        const description = (element as any).description;
        return description ? description : WorkflowDisplayUtil.getTitle(element);
    }

    static isStepDefinitionExpanded = (integration: Integration, stepUuid: string, selectedUuid: string | undefined): boolean => {
        const expandedUuids: string[] = [];
        if (selectedUuid) {
            expandedUuids.push(...WorkflowDisplayUtil.getParentStepDefinitions(integration, selectedUuid));
        }
        return expandedUuids.includes(stepUuid);
    }

    static getParentStepDefinitions = (integration: Integration, uuid: string): string[] => {
        const result: string[] = [];
        let meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, uuid);
        let i = 0;
        while (meta && meta.step?.dslName !== 'FromDefinition' && i < 100) {
            i++;
            if (meta.step?.dslName === 'StepDefinition') {
                result.push(meta.step.uuid);
            }
            if (meta.parentUuid) {
                meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, meta.parentUuid);
            } else {
                break;
            }
        }
        return result;
    }

    static setIntegrationVisibility = (integration: Integration, selectedUuid: string | undefined): Integration => {
        const clone: Integration = WorkflowUtil.cloneIntegration(integration);
        const expandedUuids: string[] = [];
        if (selectedUuid) {
            expandedUuids.push(...WorkflowDisplayUtil.getParentStepDefinitions(integration, selectedUuid));
        }

        const flows: any[] = [];
        for (const flow of clone.spec.flows || []) {
            if (flow.dslName !== 'RouteDefinition') {
                flows.push(flow);
            } else {
                const visibleRoute = WorkflowDisplayUtil.setElementVisibility(flow, true, expandedUuids);
                if (Object.keys(visibleRoute).length !== 0) {
                    flows.push(visibleRoute);
                }
            }
        }

        clone.spec.flows = flows;
        return clone;
    }

    static setElementVisibility = (step: WorkflowElement, showChildren: boolean, expandedUuids: string[]): WorkflowElement => {
        const result = WorkflowDefinitionApi.createStep(step.dslName, step);
        result.showChildren = showChildren;
        if (result.dslName === 'StepDefinition') {
            showChildren = expandedUuids.includes(result.uuid);
        }

        const elementChildDefinition = WorkflowDefinitionApiExt.getElementChildrenDefinition(step.dslName);
        for (const element of elementChildDefinition) {
            const camelElement = WorkflowDefinitionApiExt.getElementChildren(step, element);
            if (element.multiple) {
                (result as any)[element.name] = WorkflowDisplayUtil.setElementsVisibility((result as any)[element.name], showChildren, expandedUuids)
            } else {
                const prop = (result as any)[element.name];
                if (prop && Object.prototype.hasOwnProperty.call(prop, 'uuid')) {
                    (result as any)[element.name] = WorkflowDisplayUtil.setElementVisibility(camelElement[0], showChildren,expandedUuids)
                }
            }
        }
        return result;
    }

    static setElementsVisibility = (steps: WorkflowElement[] | undefined, showChildren: boolean, expandedUuids: string[]): WorkflowElement[] => {
        const result: WorkflowElement[] = [];
        if (steps) {
            for (const step of steps) {
                result.push(WorkflowDisplayUtil.setElementVisibility(step, showChildren, expandedUuids));
            }
        }
        return result;
    }
}