
import { Integration, WorkflowElement} from '../model/integration-defintion';
import { WorkflowDefinitionApi } from './workflow-definition-api';
import {
    BeanFactoryDefinition,
    RouteConfigurationDefinition, RouteTemplateDefinition,
} from '../model/workflow-definition';
import { ComponentProperty } from '../model/component-models.ts';
import { ComponentApi } from './component-api';
import {WorkflowMetadataApi} from '../model/workflow-metadata';
import { WorkflowDefinitionApiExt } from './workflow-definition-api-ext';
import { v4 as uuidv4 } from 'uuid';

export class WorkflowUtil {
    private constructor() {
    }

    static cloneIntegration = (integration: Integration): Integration => {
        const clone = JSON.parse(JSON.stringify(integration));
        const int: Integration = new Integration({ ...clone });
        const flows: any[] = [];

        for (const flow of int.spec.flows || []) {
            flows.push(WorkflowDefinitionApi.createStep(flow.dslName, flow));
        }

        for (const routeConfiguration of int.spec.flows?.filter(flow => flow.dslName === 'RouteConfiguration') || []) {
            const newRouteConfiguration = WorkflowUtil.cloneRouteConfiguration(routeConfiguration);
            flows.push(newRouteConfiguration);
        }

        for (const routeTemplate of int.spec.flows?.filter(flow => flow.dslName === 'RouteTemplateConfiguration') || []) {
            const newRouteTemplate = WorkflowUtil.cloneRouteTemplate(routeTemplate);
            flows.push(newRouteTemplate);
        }

        int.spec.flows = flows;
        return int;
    };

    static cloneStep = (step: WorkflowElement, generateUuids: boolean = false): WorkflowElement => {
        const clone = JSON.parse(
            JSON.stringify(step, (key, value) => {
                if (generateUuids && key === 'uuid') {
                    return uuidv4();
                } else {
                    return value;
                }
            }),
        );
        return WorkflowDefinitionApi.createStep(step.dslName, clone, true);
    };

    static cloneBean = (bean: BeanFactoryDefinition): BeanFactoryDefinition => {
        const clone = JSON.parse(JSON.stringify(bean));
        const newBean = new BeanFactoryDefinition(clone);
        newBean.uuid = bean.uuid;
        return newBean;
    };

    static cloneRouteConfiguration = (
        routeConfiguration: RouteConfigurationDefinition,
    ): RouteConfigurationDefinition => {
        const clone = JSON.parse(JSON.stringify(routeConfiguration));
        const RouteConfiguration = new RouteConfigurationDefinition(clone);
        RouteConfiguration.uuid = routeConfiguration.uuid;
        return RouteConfiguration;
    };
    static cloneRouteTemplate = (
        routeTemplate: RouteTemplateDefinition,
    ): RouteTemplateDefinition => {
        const clone = JSON.parse(JSON.stringify(routeTemplate));
        const RouteConfiguration = new RouteTemplateDefinition(clone);
        RouteConfiguration.uuid = routeTemplate.uuid;
        return RouteConfiguration;
    };

    static capitalizeName = (name: string): string => {
        if (name.length === 0) {
            return name;
        }
        return name[0].toUpperCase() + name.substring(1);
    };

    static normalizeName = (name: string, separator: string, firstSmall: boolean): string => {
        if (name.length === 0) return name;
        const res = name
            .split(separator)
            .map(value => WorkflowUtil.capitalizeName(value))
            .join('');
        return firstSmall ? res[0].toLowerCase() + res.substring(1) : res;
    };

    static normalizeBody = (name: string, body: any, clone: boolean): any => {
        if (body && Object.keys(body).length > 0) {
            const oldKey = Object.keys(body)[0];
            const key = WorkflowUtil.normalizeName(oldKey, '-', true);
            return !clone && key === name ? { [key]: body[oldKey] } : body;
        } else {
            return {};
        }
    };

    static normalizeObject = (body: any): any => {
        if (Array.isArray(body)) {
            return body.map(value => (typeof value === 'object' ? WorkflowUtil.normalizeObject(value) : value));
        } else if (typeof body === 'object') {
            const result: any = {};
            for (const key in body) {
                // eslint-disable-next-line no-prototype-builtins
                if (body?.hasOwnProperty(key)) {
                    const newKey = WorkflowUtil.normalizeName(key, '-', true);
                    const value = body[key];
                    if (typeof value === 'object' || Array.isArray(value)) {
                        result[newKey] = WorkflowUtil.normalizeObject(value);
                    } else {
                        result[newKey] = value;
                    }
                }
            }
            return result;
        } else {
            return body;
        }
    };



    static getComponentProperties = (element: any): ComponentProperty[] => {
        const uri: string = (element as any).uri;
        const name = ComponentApi.getComponentNameFromUri(uri);

        if (name) {
            const component = ComponentApi.findByName(name);
            const type: 'consumer' | 'producer' = ['FromDefinition', 'PollDefinition'].includes(element.dslName) ? 'consumer' : 'producer'
            return component ? ComponentApi.getComponentProperties(component?.component.name, type) : [];
        } else {
            return [];
        }
    };

    static checkRequired = (element: WorkflowElement): [boolean, string[]] => {
        const result: [boolean, string[]] = [true, []];
        const className = element.dslName;
        let elementMeta = WorkflowMetadataApi.getWorkflowModelMetadataByClassName(className);

        if (elementMeta === undefined && className.endsWith('Expression')) {
            elementMeta = WorkflowMetadataApi.getWorkflowLanguageMetadataByClassName(className);
        }

        if (elementMeta) {
            for (const property of elementMeta.properties.filter(p => p.required)) {
                const value = (element as any)[property.name];
                if (property.type === 'string' && !property.isArray && (value === undefined || !value.toString().trim())) {
                    result[0] = false;
                    result[1].push(`${property.displayName} is required`);
                } else if (['ExpressionSubElementDefinition', 'ExpressionDefinition'].includes(property.type)) {
                    const expressionMeta = WorkflowMetadataApi.getWorkflowModelMetadataByClassName('ExpressionDefinition');
                    const expressionCheck = expressionMeta && value !== undefined && expressionMeta?.properties.some(ep => {
                        const expValue = value[ep.name];
                        if (expValue) {
                            const checkedExpression = WorkflowUtil.checkRequired(expValue);
                            return checkedExpression[0];
                        }
                        return false;
                    });
                    result[0] = !!expressionCheck;
                    if (!expressionCheck) {
                        result[1].push('Expression is not defined');
                    }
                }
            }
        }

        if (className === 'FromDefinition' || className === 'ToDefinition') {
            const requiredProperties = WorkflowUtil.getComponentProperties(element).filter(p => p.required);
            for (const property of requiredProperties) {
                const value = WorkflowDefinitionApiExt.getParametersValue(element, property.name, property.kind === 'path');
                if (value === undefined || (property.type === 'string' && value.toString().trim().length === 0)) {
                    result[0] = false;
                    result[1].push(`${property.displayName} is required`);
                }
            }
            const secretProperties = WorkflowUtil.getComponentProperties(element).filter(p => p.secret);
            for (const property of secretProperties) {
                const value = WorkflowDefinitionApiExt.getParametersValue(element, property.name, property.kind === 'path');
                if (value !== undefined && property.type === 'string'
                    && (!value?.toString().trim()?.startsWith("{{") || !value?.toString().trim()?.endsWith('}}'))) {
                    result[0] = false;
                    result[1].push(`${property.displayName} is set in plain text`);
                }
            }
        }
        if (result[1] && result[1].length > 0) {
            result[0] = false;
        }
        return result;
    };


    static findPlaceholdersInObject = (item: any, result: Set<string> = new Set<string>()): Set<string> => {
        if (typeof item === 'object') {
            for (const value of Object.values(item)) {
                if (value == undefined) {
                    continue;
                } else if (Array.isArray(value)) {
                    WorkflowUtil.findPlaceholdersInArray(value, result);
                } else if (typeof value === 'object') {
                    WorkflowUtil.findPlaceholdersInObject(value, result);
                } else {
                    const placeholder = WorkflowUtil.findPlaceholder(value.toString());
                    if (placeholder[0] && placeholder[1]) {
                        result.add(placeholder[1]);
                    }
                }
            }
        } else {
            const placeholder = WorkflowUtil.findPlaceholder(item.toString());
            if (placeholder[0] && placeholder[1]) {
                result.add(placeholder[1]);
            }
        }
        return result;
    };

    static findPlaceholdersInArray = (
        items: any[] | undefined,
        result: Set<string> = new Set<string>(),
    ): Set<string> => {
        if (items) {
            for (const item of items) {
                if (typeof item === 'object') {
                    WorkflowUtil.findPlaceholdersInObject(item, result);
                } else {
                    const placeholder = WorkflowUtil.findPlaceholder(item.toString());
                    if (placeholder[0] && placeholder[1]) {
                        result.add(placeholder[1]);
                    }
                }
            }
        }
        return result;
    };

    static isContainer(element:WorkflowElement){
        return element? false:false
    }

    static findPlaceholder = (value: string): [boolean, string?] => {
        const val = value?.trim();
        const result = val?.includes('{{') && val?.includes('}}');
        const start = val?.search('{{') + 2;
        const end = val?.search('}}');
        const placeholder = val?.substring(start, end)?.trim();
        return [result, placeholder];
    };

}