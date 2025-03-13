
import { WorkflowMetadataApi, ElementMeta, Languages, PropertyMeta } from '../model/workflow-metadata';
import { WorkflowUtil } from './workflow-util';
import {
    BeanFactoryDefinition,
    ExpressionDefinition,
    RouteDefinition,
    RestDefinition,
    RouteConfigurationDefinition, FromDefinition, RouteTemplateDefinition,
} from '../model/workflow-definition';
import {WorkflowElement, WorkflowElementMeta, Integration } from '../model/integration-defintion.ts';
import { WorkflowDefinitionApi } from './workflow-definition-api.ts';

const coreRoutishElements = ['RouteConfigurationDefinition', 'RouteTemplateDefinition', 'RouteDefinition']

export class ChildElement {
    constructor(public name: string = '', public className: string = '', public multiple: boolean = false) {}
}

export class WorkflowDefinitionApiExt {
    private constructor() {}

    // additional helper functions for more readability
    private static getFlowsOfType(integration: Integration, type: string): WorkflowElement[] {
        return integration.spec.flows?.filter(flow => flow.dslName === type) ?? [];
    }

    private static getFlowsOfTypes(integration: Integration, types: string[]): WorkflowElement[] {
        return integration.spec.flows?.filter(flow => types.includes(flow.dslName)) ?? [];
    }

    private static getFlowsNotOfTypes(integration: Integration, types: string[]): any[] {
        return integration.spec.flows?.filter(flow => !types.includes(flow.dslName)) ?? [];
    }

    static replaceFromInIntegration = (integration: Integration, fromId: string, newFrom: FromDefinition): Integration => {
        const flows: any = [];
        WorkflowDefinitionApiExt.getFlowsNotOfTypes(integration, ['RouteDefinition', 'RouteTemplateDefinition']).forEach(notRoutes =>
            flows.push(notRoutes),
        );
        WorkflowDefinitionApiExt.getFlowsOfTypes(integration, ['RouteDefinition', 'RouteTemplateDefinition']).map(r => {
            if (r.dslName === 'RouteDefinition') {
                const route = (r as RouteDefinition);
                if (route.from.id === fromId) {
                    newFrom.steps = [...route.from.steps];
                    route.from = newFrom;
                    flows.push(route);
                } else {
                    flows.push(route);
                }
            } else if (r.dslName === 'RouteTemplateDefinition') {
                const routeTemplate = (r as RouteTemplateDefinition);
                if (routeTemplate.route?.from.id === fromId) {
                    // eslint-disable-next-line no-unsafe-optional-chaining
                    newFrom.steps = [...routeTemplate.route?.from.steps];
                    routeTemplate.route.from = newFrom;
                    flows.push(routeTemplate);
                } else {
                    flows.push(routeTemplate);
                }
            }

        })
        integration.spec.flows = flows;
        return integration;
    };

    static addStepToIntegration = (integration: Integration, step: WorkflowElement, parentId: string, position?: number,): Integration => {
        if (step.dslName === 'RouteDefinition') {
            integration.spec.flows?.push(step as RouteDefinition);
        } else {
            const flows: any = [];
            WorkflowDefinitionApiExt.getFlowsNotOfTypes(integration, coreRoutishElements).forEach(bean =>
                flows.push(bean),
            );
            const routes = WorkflowDefinitionApiExt.addStepToSteps(WorkflowDefinitionApiExt.getFlowsOfType(integration, 'RouteDefinition'), step, parentId, position,);
            flows.push(...routes);
            const routeConfigurations = WorkflowDefinitionApiExt.addStepToSteps(WorkflowDefinitionApiExt.getFlowsOfType(integration, 'RouteConfigurationDefinition'),step, parentId, position,);
            flows.push(...routeConfigurations);
            const routeTemplates = WorkflowDefinitionApiExt.addStepToSteps(WorkflowDefinitionApiExt.getFlowsOfType(integration, 'RouteTemplateDefinition'),step, parentId, position,);
            flows.push(...routeTemplates);
            integration.spec.flows = flows;
        }
        return integration;
    };

    static addStepToStep = (step: WorkflowElement, stepAdded: WorkflowElement, parentId: string, position: number = -1,): WorkflowElement => {
        const result = WorkflowUtil.cloneStep(step);
        const children = WorkflowDefinitionApiExt.getElementChildrenDefinition(result.dslName);
        let added = false;

        // Check all fields except steps
        for (const child of children.filter(child => child.name !== 'steps') ?? []) {
            if (result.uuid === parentId) {
                if (child.className === stepAdded.dslName) {
                    added = true;
                    if (child.multiple) {
                        (result as any)[child.name].push(stepAdded);
                    } else {
                        (result as any)[child.name] = stepAdded;
                    }
                }
            } else {
                const fieldValue = (result as any)[child.name];
                if (child.multiple) {
                    (result as any)[child.name] = WorkflowDefinitionApiExt.addStepToSteps((result as any)[child.name], stepAdded, parentId, position,);
                } else if (fieldValue) {
                    (result as any)[child.name] = WorkflowDefinitionApiExt.addStepToStep(fieldValue, stepAdded, parentId, position);
                }
            }
        }

        // Then steps
        const steps = children.filter(child => child.name === 'steps');
        if (!added && steps && result.uuid === parentId) {
            if (position > -1) {
                (result as any).steps.splice(position, 0, stepAdded);
            } else {
                (result as any).steps.push(stepAdded);
            }
        } else if (!added && steps && (result as any).steps) {
            (result as any).steps = WorkflowDefinitionApiExt.addStepToSteps((result as any).steps, stepAdded, parentId, position);
        }

        return result;
    };

    static addStepToSteps = (steps: WorkflowElement[], step: WorkflowElement, parentId: string, position?: number,): WorkflowElement[] => {
        const result: WorkflowElement[] = [];
        for (const element of steps) {
            const newStep = WorkflowDefinitionApiExt.addStepToStep(element, step, parentId, position);
            result.push(newStep);
        }
        return result;
    };

    static findElementInIntegration = (integration: Integration, uuid: string): WorkflowElement | undefined => {
        return WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, uuid)?.step;
    };

    static findElementMetaInIntegration = (integration: Integration, uuid: string): WorkflowElementMeta => {
        const i = WorkflowUtil.cloneIntegration(integration);
        const routes = i.spec.flows?.filter(flow => coreRoutishElements.includes(flow.dslName),);
        return WorkflowDefinitionApiExt.findElementInElements(routes, uuid);
    };

    static findElementPathUuids = (integration: Integration, uuid: string): string[] => {
        const result: string[] = [];
        let meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, uuid);
        if (meta && meta.parentUuid) {
            while (meta.step?.dslName !== 'FromDefinition') {
                if (meta.parentUuid) {
                    result.push(meta.parentUuid);
                    meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, meta.parentUuid);
                } else {
                    break;
                }
            }
        }
        return result;
    };

    static findTopRouteElement = (integration: Integration, uuid: string): WorkflowElement | undefined => {
        const result: string[] = [];
        let meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, uuid);
        if (meta) {
            while (meta.parentUuid !== undefined) {
                if (meta.parentUuid) {
                    result.push(meta.parentUuid);
                    meta = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, meta.parentUuid);
                } else {
                    break;
                }
            }
        }
        const last = result.at(-1);
        return last ? WorkflowDefinitionApiExt.findElementInIntegration(integration, last): undefined
    };

    static findElementInElements = (steps: WorkflowElement[] | undefined, uuid: string, result: WorkflowElementMeta = new WorkflowElementMeta(undefined, undefined, undefined),
                                    parentUuid?: string,): WorkflowElementMeta => {
        if (result?.step !== undefined) {
            return result;
        }

        if (steps !== undefined) {
            for (let index = 0, step: WorkflowElement; (step = steps[index]); index++) {
                if (step.uuid === uuid) {
                    result = new WorkflowElementMeta(step, parentUuid, index);
                    break;
                } else {
                    const ce = WorkflowDefinitionApiExt.getElementChildrenDefinition(step.dslName);
                    for (const e of ce) {
                        const cel = WorkflowDefinitionApiExt.getElementChildren(step, e);
                        if (e.multiple) {
                            result = WorkflowDefinitionApiExt.findElementInElements(cel, uuid, result, step.uuid);
                        } else {
                            const prop = (step as any)[e.name];
                            if (prop && Object.prototype.hasOwnProperty.call(prop, 'uuid')) {
                                result = WorkflowDefinitionApiExt.findElementInElements([prop], uuid, result, step.uuid);
                            }
                        }
                    }
                }
            }
        }
        return new WorkflowElementMeta(result?.step, result?.parentUuid, result?.position);
    };

    static hasElementWithId = (integration: Integration, id: string): number => {
        return WorkflowDefinitionApiExt.checkIfHasId(integration, id, 0);
    };

    static checkIfHasId = (obj: object, id: string, counter: number): number => {
        for (const propName in obj) {
            const prop = (obj as any)[propName];
            if (propName === 'id' && id === prop) {
                counter++;
                counter = WorkflowDefinitionApiExt.checkIfHasId(prop, id, counter);
            } else if (typeof prop === 'object' && prop !== null) {
                counter = WorkflowDefinitionApiExt.checkIfHasId(prop, id, counter);
            } else if (Array.isArray(prop)) {
                for (const element of prop) {
                    WorkflowDefinitionApiExt.checkIfHasId(element, id, counter);
                }
            }
        }
        return counter;
    };

    static findElementById = (integration: Integration, id: string): WorkflowElement | undefined => {
        return WorkflowDefinitionApiExt.findElementsById(integration, id, [])?.at(0);
    };

    static findElementsById = (obj: object, id: string, elements: WorkflowElement[]): WorkflowElement[] => {
        for (const propName in obj) {
            const prop = (obj as any)[propName];
            if (propName === 'id' && id === prop) {
                elements.push(obj as WorkflowElement)
                elements = WorkflowDefinitionApiExt.findElementsById(prop, id, elements);
            } else if (typeof prop === 'object' && prop !== null) {
                elements = WorkflowDefinitionApiExt.findElementsById(prop, id, elements);
            } else if (Array.isArray(prop)) {
                for (const element of prop) {
                    elements = WorkflowDefinitionApiExt.findElementsById(element, id, elements);
                }
            }
        }
        return elements;
    };

    static moveRouteElement = (integration: Integration, source: string, target: string, asChild: boolean,): Integration => {
        const sourceFindStep = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, source);
        const sourceStep = sourceFindStep.step;
        const sourceUuid = sourceStep?.uuid;
        const targetFindStep = WorkflowDefinitionApiExt.findElementMetaInIntegration(integration, target);
        const parentUuid = targetFindStep.parentUuid;
        if (sourceUuid && parentUuid && sourceStep && !WorkflowDefinitionApiExt.findElementPathUuids(integration, target).includes(source)) {
            WorkflowDefinitionApiExt.deleteStepFromIntegration(integration, sourceUuid);
            if (asChild) {
                return WorkflowDefinitionApiExt.addStepToIntegration(
                    integration,
                    sourceStep,
                    target,
                    (targetFindStep?.step as any)?.steps?.length,
                );
            } else {
                switch (targetFindStep.step?.dslName) {
                    case 'when':
                        return WorkflowDefinitionApiExt.addStepToIntegration(integration, sourceStep, targetFindStep.step?.uuid, undefined);
                    case 'otherwise':
                        return WorkflowDefinitionApiExt.addStepToIntegration(integration, sourceStep, targetFindStep.step?.uuid, undefined);
                    default:
                        return WorkflowDefinitionApiExt.addStepToIntegration(integration, sourceStep, parentUuid, targetFindStep.position);
                }
            }
        }
        return integration;
    };

    static deleteStepFromIntegration = (integration: Integration, uuidToDelete: string): Integration => {
        const flows: any[] =
            integration.spec.flows?.filter(flow => !coreRoutishElements.includes(flow.dslName),) ?? [];
        const routes = WorkflowDefinitionApiExt.deleteStepFromSteps(
            integration.spec.flows?.filter(flow => coreRoutishElements.includes(flow.dslName),),
            uuidToDelete,
        );
        flows.push(...routes);
        integration.spec.flows = flows;
        return integration;
    };

    static deleteStepFromStep = (step: WorkflowElement, uuidToDelete: string): WorkflowElement => {
        const result = WorkflowDefinitionApi.createStep(step.dslName, step);
        const ce = WorkflowDefinitionApiExt.getElementChildrenDefinition(step.dslName);
        for (const e of ce) {
            const cel = WorkflowDefinitionApiExt.getElementChildren(step, e);
            if (e.multiple) {
                (result as any)[e.name] = WorkflowDefinitionApiExt.deleteStepFromSteps((result as any)[e.name], uuidToDelete);
            } else {
                const prop = (result as any)[e.name];
                // eslint-disable-next-line no-prototype-builtins
                if (prop?.hasOwnProperty('uuid')) {
                    if (prop.uuid === uuidToDelete) {
                        delete (result as any)[e.name];
                    } else {
                        (result as any)[e.name] = WorkflowDefinitionApiExt.deleteStepFromStep(cel[0], uuidToDelete);
                    }
                }
            }
        }
        return result;
    };

    static deleteStepFromSteps = (steps: WorkflowElement[] | undefined, uuidToDelete: string): WorkflowElement[] => {
        const result: WorkflowElement[] = [];
        if (steps !== undefined) {
            for (const step of steps) {
                if (step.uuid !== uuidToDelete) {
                    const newStep = WorkflowDefinitionApiExt.deleteStepFromStep(step, uuidToDelete);
                    result.push(newStep);
                }
            }
        }
        return result;
    };

    

    static addRouteConfigurationToIntegration = (
        integration: Integration,
        routeConfiguration: RouteConfigurationDefinition,
    ): Integration => {
        integration.spec.flows?.push(routeConfiguration);
        return integration;
    };

    static deleteRouteConfigurationFromIntegration = (
        integration: Integration,
        routeConfiguration: RouteConfigurationDefinition,
    ): Integration => {
        const newFlows: any[] = [];
        const flows: any[] = integration.spec.flows ?? [];
        newFlows.push(...flows.filter(flow => flow.dslName !== 'RouteConfigurationDefinition'));
        newFlows.push(
            ...flows.filter(
                flow => flow.dslName === 'RouteConfigurationDefinition' && flow.uuid !== routeConfiguration.uuid,
            ),
        );
        integration.spec.flows = newFlows;
        return integration;
    };

    static updateRouteConfigurationToIntegration = (integration: Integration, e: WorkflowElement): Integration => {
        const elementClone = WorkflowUtil.cloneStep(e);
        const integrationClone: Integration = WorkflowUtil.cloneIntegration(integration);

        integrationClone.spec.flows = integration.spec.flows?.map(flow => {
            if (flow.dslName === 'RouteConfigurationDefinition') {
                const route = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as RouteConfigurationDefinition;
                return WorkflowDefinitionApi.createRouteConfigurationDefinition(route);
            }
            return flow;
        });
        return integrationClone;
    };

    static addRouteTemplateToIntegration = (
        integration: Integration,
        routeTemplate: RouteTemplateDefinition,
    ): Integration => {
        integration.spec.flows?.push(routeTemplate);
        return integration;
    };

    static deleteRouteTemplateFromIntegration = (
        integration: Integration,
        routeTemplate: RouteTemplateDefinition,
    ): Integration => {
        const newFlows: any[] = [];
        const flows: any[] = integration.spec.flows ?? [];
        newFlows.push(...flows.filter(flow => flow.dslName !== 'RouteTemplateDefinition'));
        newFlows.push(
            ...flows.filter(
                flow => flow.dslName === 'RouteTemplateDefinition' && flow.uuid !== routeTemplate.uuid,
            ),
        );
        integration.spec.flows = newFlows;
        return integration;
    };

    static updateRouteTemplateToIntegration = (integration: Integration, e: WorkflowElement): Integration => {
        const elementClone = WorkflowUtil.cloneStep(e);
        const integrationClone: Integration = WorkflowUtil.cloneIntegration(integration);

        integrationClone.spec.flows = integration.spec.flows?.map(flow => {
            if (flow.dslName === 'RouteTemplateDefinition') {
                const route = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as RouteTemplateDefinition;
                return WorkflowDefinitionApi.createRouteTemplateDefinition(route);
            }
            return flow;
        });
        return integrationClone;
    };

    static addRestToIntegration = (integration: Integration, rest: RestDefinition): Integration => {
        integration.spec.flows?.push(rest);
        return integration;
    };

    static addRestMethodToIntegration = (
        integration: Integration,
        method: WorkflowElement,
        restUuid: string,
    ): Integration => {
        const flows: any[] = [];
        const methodFunctions: { [key: string]: (rest: RestDefinition, method: WorkflowElement) => void } = {
            GetDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.get = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.get, method);
            },
            PostDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.post = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.post, method);
            },
            PutDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.put = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.put, method);
            },
            PatchDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.patch = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.patch, method);
            },
            DeleteDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.delete = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.delete, method);
            },
            HeadDefinition: (rest: RestDefinition, method: WorkflowElement) => {
                rest.head = WorkflowDefinitionApiExt.addRestMethodToRestMethods(rest.head, method);
            },
        };

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName === 'RestDefinition') {
                if (flow.uuid !== restUuid) {
                    flows.push(flow);
                } else {
                    if (method.dslName in methodFunctions) {
                        methodFunctions[method.dslName](flow, method);
                    }
                    flows.push(flow);
                }
            } else {
                flows.push(flow);
            }
        }

        integration.spec.flows = flows;
        return integration;
    };

    static addRestMethodToRestMethods = (methods: WorkflowElement[] = [], method: WorkflowElement): WorkflowElement[] => {
        const elements: WorkflowElement[] = [];
        for (const e of methods) {
            if (e.uuid === method.uuid) {
                elements.push(method);
            } else {
                elements.push(e);
            }
        }
        if (elements.filter(e => e.uuid === method.uuid).length === 0) {
            elements.push(method);
        }
        return elements;
    };

    static findRestMethodParent = (integration: Integration, method: WorkflowElement): string | undefined => {
        const rests: RestDefinition[] = integration.spec.flows?.filter(flow => flow.dslName === 'RestDefinition') ?? [];
        const methodTypes = ['get', 'post', 'put', 'patch', 'delete', 'head'];

        for (const rest of rests) {
            for (const type of methodTypes) {
                if (
                    method.dslName.toLowerCase() === `${type}definition` &&
                    (rest as any)[type]?.find((m: any) => m.uuid === method.uuid)
                ) {
                    return rest.uuid;
                }
            }
        }
    };

    static deleteRestConfigurationFromIntegration = (integration: Integration): Integration => {
        const flows: any[] = [];

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName !== 'RestConfigurationDefinition') {
                flows.push(flow);
            }
        }

        integration.spec.flows = flows;
        return integration;
    };

    static deleteRestFromIntegration = (integration: Integration, restUuid?: string): Integration => {
        const flows: any[] = [];

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName !== 'RestDefinition' || flow.uuid !== restUuid) {
                flows.push(flow);
            }
        }

        integration.spec.flows = flows;
        return integration;
    };

    static deleteRestMethodFromIntegration = (integration: Integration, methodUuid?: string): Integration => {
        const flows: any[] = [];
        const methods = ['get', 'post', 'put', 'patch', 'delete', 'head'];

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName === 'RestDefinition') {
                for (const method of methods) {
                    if (flow[method]) {
                        flow[method] = flow[method].filter((item: any) => item.uuid !== methodUuid);
                    }
                }
            }
            flows.push(flow);
        }

        integration.spec.flows = flows;
        return integration;
    };

    static getExpressionLanguageName = (expression: ExpressionDefinition | undefined): string | undefined => {
        let result: string | undefined = undefined;
        if (expression) {
            for (const fieldName in expression) {
                if ((expression as any)[fieldName] === undefined) {
                    continue;
                }

                const lang = Languages.find((value: [string, string, string]) => value[0] === fieldName);
                if (lang) {
                    const camelLangMetadata = WorkflowMetadataApi.getWorkflowLanguageMetadataByName(lang[0]);
                    if (camelLangMetadata?.name) {
                        result = camelLangMetadata.name;
                        break;
                    }
                }
            }
        }
        return result;
    };

    static getExpressionLanguageClassName = (expression: ExpressionDefinition | undefined): string | undefined => {
        let result: string | undefined = undefined;
        if (expression) {
            for (const fieldName in expression) {
                if ((expression as any)[fieldName] === undefined) {
                    continue;
                }

                const lang = Languages.find((value: [string, string, string]) => value[0] === fieldName);
                if (lang) {
                    const camelLangMetadata = WorkflowMetadataApi.getWorkflowLanguageMetadataByName(lang[0]);
                    if (camelLangMetadata?.className) {
                        result = camelLangMetadata.className;
                        break;
                    }
                }
            }
        }
        return result;
    };

    static getDataFormat = (element: WorkflowElement | undefined): ElementMeta | undefined => {
        let result: ElementMeta | undefined = undefined;
        if (element) {
            Object.keys(element).forEach(fieldName => {
                const df = WorkflowMetadataApi.getWorkflowDataFormatMetadataByName(fieldName);
                result =  (element as any)[fieldName] ? df : result;
            });
        }
        return result;
    }

    static getExpressionValue = (expression: ExpressionDefinition | undefined): WorkflowElement | undefined => {
        const language = WorkflowDefinitionApiExt.getExpressionLanguageName(expression);
        if (language) {
            return (expression as any)[language];
        } else {
            return undefined;
        }
    };

    static updateIntegrationRestElement = (integration: Integration, e: WorkflowElement): Integration => {
        const int: Integration = WorkflowUtil.cloneIntegration(integration);
        const flows: WorkflowElement[] = [];

        const methods = ['get', 'post', 'put', 'patch', 'delete', 'head'];

        const isRest = (flow: any) => flow.dslName === 'RestDefinition' && flow.uuid === e.uuid;
        const isRestConfig = (flow: any) => flow.dslName === 'RestConfigurationDefinition' && flow.uuid === e.uuid;

        const isSingleRest = integration.spec.flows?.filter(isRest).length === 1;
        const isSingleRestConfig = integration.spec.flows?.filter(isRestConfig).length === 1;

        for (const flow of integration.spec.flows ?? []) {
            if ((isSingleRest && isRest(flow)) || (isSingleRestConfig && isRestConfig(flow))) {
                flows.push(WorkflowUtil.cloneStep(e));
            } else if (flow.dslName === 'RestDefinition') {
                for (const method of methods) {
                    if (flow[method]) {
                        for (let i = 0; i < flow[method].length; i++) {
                            if (flow[method][i].uuid === e.uuid) {
                                flow[method][i] = e;
                            }
                        }
                    }
                }
                flows.push(flow);
            } else {
                flows.push(flow);
            }
        }

        int.spec.flows = flows;
        return int;
    };

    static updateIntegrationRouteElement = (integration: Integration, e: WorkflowElement): Integration => {
        const elementClone = WorkflowUtil.cloneStep(e);
        const int: Integration = WorkflowUtil.cloneIntegration(integration);
        const flows: WorkflowElement[] = [];

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName === 'RouteDefinition') {
                const route = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as RouteDefinition;
                flows.push(WorkflowDefinitionApi.createRouteDefinition(route));
            } else if (flow.dslName === 'RouteConfigurationDefinition') {
                const routeConfiguration = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as RouteConfigurationDefinition;
                flows.push(WorkflowDefinitionApi.createRouteConfigurationDefinition(routeConfiguration));
            } else if (flow.dslName === 'RouteTemplateDefinition') {
                const routeTemplate = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as RouteTemplateDefinition;
                flows.push(WorkflowDefinitionApi.createRouteTemplateDefinition(routeTemplate));
            } else {
                flows.push(flow);
            }
        }

        int.spec.flows = flows;
        return int;
    };

    static updateIntegrationBeanElement = (integration: Integration, e: WorkflowElement): Integration => {
        const elementClone = WorkflowUtil.cloneStep(e);
        const int: Integration = WorkflowUtil.cloneIntegration(integration);
        const flows: WorkflowElement[] = [];

        for (const flow of integration.spec.flows ?? []) {
            if (flow.dslName === 'Beans') {
                const route = WorkflowDefinitionApiExt.updateElement(flow, elementClone) as BeanFactoryDefinition;
                flows.push(WorkflowDefinitionApi.createBeanFactoryDefinition(route));
            } else {
                flows.push(flow);
            }
        }

        int.spec.flows = flows;
        return int;
    };

    static updateElement = (element: WorkflowElement, e: WorkflowElement): WorkflowElement => {
        if (element.uuid === e.uuid) {
            return e;
        }
        const result: any = { ...element };
        for (const key in result) {
            if (result[key] instanceof WorkflowElement) {
                result[key] = WorkflowDefinitionApiExt.updateElement(result[key], e);
            } else if (Array.isArray(result[key])) {
                result[key] = WorkflowDefinitionApiExt.updateElements(result[key], e);
            }
        }
        return result as WorkflowElement;
    };

    static updateElements = (elements: WorkflowElement[], e: WorkflowElement): WorkflowElement[] => {
        const result: any[] = [];
        for (const element of elements) {
            if (typeof element === 'object') {
                const newElement = WorkflowDefinitionApiExt.updateElement(element, e);
                result.push(newElement);
            } else {
                result.push(element);
            }
        }
        return result;
    };

    static getElementProperties = (className: string | undefined): PropertyMeta[] => {
        const result: PropertyMeta[] = [];
        let uri: any = undefined;
        let expression: any = undefined;
        let parameters: any = undefined;

        if (className) {
            const properties =
                className.endsWith('Definition') || className.endsWith('BuilderRef') || className.endsWith('Config')
                    ? WorkflowMetadataApi.getWorkflowModelMetadataByClassName(className)?.properties
                    : className.endsWith('DataFormat')
                        ? WorkflowMetadataApi.getWorkflowDataFormatMetadataByClassName(className)?.properties
                        : WorkflowMetadataApi.getWorkflowLanguageMetadataByClassName(className)?.properties;

            if (properties) {
                for (const p of properties.filter(p => p.name !== 'steps' && p.name !== 'configurationRef')) {
                    switch (p.name) {
                        case 'uri':
                            uri = p;
                            break;
                        case 'expression':
                            expression = p;
                            break;
                        case 'parameters':
                            parameters = p;
                            break;
                        default:
                            result.push(p);
                    }
                }
            }
        }

        if (uri) {
            result.unshift(uri);
        }
        if (expression) {
            result.unshift(expression);
        }
        if (parameters) {
            result.push(parameters);
        }

        return result;
    };

    static getElementPropertiesByName = (name: string): PropertyMeta[] => {
        const model = WorkflowMetadataApi.getWorkflowModelMetadataByName(name);
        if (model) {
            return WorkflowDefinitionApiExt.getElementProperties(model.className);
        }
        const language = WorkflowMetadataApi.getWorkflowLanguageMetadataByName(name);
        if (language) {
            return WorkflowDefinitionApiExt.getElementProperties(language.className);
        }
        const dataFormat = WorkflowMetadataApi.getWorkflowDataFormatMetadataByName(name);
        if (dataFormat) {
            return WorkflowDefinitionApiExt.getElementProperties(dataFormat.className);
        }
        return [];
    };

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    static getParametersValue = (element: WorkflowElement | undefined, propertyName: string, _: boolean): any => {
        if (element && (element as any).parameters) {
            return (element as any).parameters[propertyName];
        }
    };

    static getElementChildrenDefinition = (dslName: string): ChildElement[] => {
        const result: ChildElement[] = [];
        const meta = WorkflowMetadataApi.getWorkflowModelMetadataByClassName(dslName);

        if (meta) {
            for (const property of meta.properties) {
                if (property.isObject && WorkflowMetadataApi.getWorkflowModelMetadataByClassName(property.type)) {
                    result.push(new ChildElement(property.name, property.type, property.isArray));
                }
            }
        }

        if (WorkflowDefinitionApi.createStep(dslName, {}).hasSteps())
            result.push(new ChildElement('steps', 'WorkflowElement', true));
        return result;
    };

    static getElementChildren = (element: WorkflowElement, child: ChildElement): WorkflowElement[] => {
        let children = (element as any)[child.name];
        if (!Array.isArray(children)) {
            children = children ? [children] : [];
        }
        return children;
    };
}