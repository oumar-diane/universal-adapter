import * as yaml from 'js-yaml';
import {WorkflowElement, Integration } from '../model/integration-defintion';
import {
    RouteConfigurationDefinition,
    RouteDefinition,
} from '../model/workflow-definition';
import { WorkflowUtil } from './workflow-util';
import { WorkflowDefinitionYamlStep } from './workflow-definition-yaml-step';

export class WorkflowDefinitionYaml {
    private constructor() {
    }

    static integrationToYaml = (integration: Integration): string => {
        const clone: any = WorkflowUtil.cloneIntegration(integration);
        const flows = integration.spec.flows;
        clone.spec.flows = flows
            ?.map((f: any) => WorkflowDefinitionYaml.cleanupElement(f))
            .filter(x => Object.keys(x).length !== 0);
        delete clone.type;
        const i = JSON.parse(JSON.stringify(clone, (key, value) => WorkflowDefinitionYaml.replacer(key, value), 3)); // fix undefined in string attributes
        return WorkflowDefinitionYaml.yamlDump(i);
    };

    static isEmpty = (value?: string): boolean => {
        return value === undefined || (value.trim && value.trim().length === 0);
    };

    static isEmptyObject(obj: any): boolean {
        // Check if it's an object and not null
        if (obj && typeof obj === 'object') {
            // Get all enumerable property names
            const keys = Object.keys(obj);
            // Get all non-enumerable property names
            const nonEnumProps = Object.getOwnPropertyNames(obj);
            // Check if there are no properties
            return keys.length === 0 && nonEnumProps.length === 0;
        }
        return false;
    }


    static cleanupElement = (element: WorkflowElement, inArray?: boolean, inSteps?: boolean): WorkflowElement => {
        const result: any = {};
        const object: any = { ...element };

        if (inArray) {
            object.inArray = inArray;
            object.inSteps = !!inSteps;
        }

        if (object.dslName === 'RouteTemplateDefinition') {
            object.route.inArray = true;
        } else if (object.dslName.endsWith('Expression')) {
            delete object.language;
            delete object.expressionName;
        } else if (object.dslName.endsWith('DataFormat')) {
            delete object.dataFormatName;
        } else if (object.dslName === 'BeanFactoryDefinition') {
            if (object.properties && Object.keys(object.properties).length === 0) {
                delete object.properties;
            }
            if (object.constructors && WorkflowDefinitionYaml.isEmptyObject(object.constructors)) {
                delete object.constructors;
            }
        } else if (['CatchDefinition', 'OnExceptionDefinition', 'OnCompletionDefinition', 'Resilience4jConfigurationDefinition'].includes(object.dslName) && object?.onWhen?.stepName !== undefined) {
            object.onWhen.stepName = 'onWhen';
        }

        delete object.uuid;
        delete object.showChildren;

        for (const [key, value] of Object.entries(object) as [string, any][]) {
            if (value instanceof WorkflowElement || (typeof value === 'object' && value?.dslName)) {
                result[key] = WorkflowDefinitionYaml.cleanupElement(value);
            } else if (Array.isArray(value)) {
                if (value.length > 0) {
                    result[key] = WorkflowDefinitionYaml.cleanupElements(value, key === 'steps');
                }
            } else if (key === 'parameters' && typeof value === 'object') {
                const parameters = Object.entries(value || {})
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    .filter(([_, v]: [string, any]) => !WorkflowDefinitionYaml.isEmpty(v))
                    .reduce((x: any, [k, v]) => ({ ...x, [k]: v }), {});
                if (Object.keys(parameters).length > 0) {
                    result[key] = parameters;
                }
            } else {
                if (!WorkflowDefinitionYaml.isEmpty(value)) {
                    result[key] = value;
                }
            }
        }
        return result as WorkflowElement;
    };

    static cleanupElements = (elements: WorkflowElement[], inSteps?: boolean): WorkflowElement[] => {
        const result: any[] = [];
        for (const element of elements) {
            if (typeof element === 'object') {
                result.push(WorkflowDefinitionYaml.cleanupElement(element, true, inSteps));
            } else {
                result.push(element);
            }
        }
        return result;
    };

    static yamlDump = (integration: any): string => {
        return yaml.dump(integration, {
            noRefs: false,
            noArrayIndent: false,
            // forceQuotes: true,
            quotingType: '"',
            sortKeys: function(a: any, b: any) {
                if (a === 'steps') return 1;
                else if (b === 'steps') return -1;
                else return 0;
            },
        });
    };

    static replacer = (key: string, value: any, isKamelet: boolean = false): any => {
        if (
            typeof value === 'object' &&
            (Object.prototype.hasOwnProperty.call(value, 'stepName') || Object.prototype.hasOwnProperty.call(value, 'inArray') || Object.prototype.hasOwnProperty.call(value, 'inSteps'))
        ) {
            const stepNameField = Object.prototype.hasOwnProperty.call(value, 'stepName') ? 'stepName' : 'step-name';
            const stepName = value[stepNameField];
            const dslName = value.dslName;
            const newValue: any = JSON.parse(JSON.stringify(value));
            delete newValue.dslName;
            delete newValue[stepNameField];

            if (
                value.inArray &&
                !value.inSteps &&
                ['intercept', 'interceptFrom', 'interceptSendToEndpoint', 'onCompletion', 'onException'].includes(
                    stepName,
                )
            ) {
                delete newValue.inArray;
                delete newValue.inSteps;
                const xValue: any = {};
                xValue[stepName] = newValue;
                return xValue;
            } else if (value.inArray && dslName === 'RouteDefinition' && !isKamelet ) { // route in RouteTemplate
                delete value?.dslName;
                delete value?.stepName;
                delete value?.inArray;
                return value;
            } else if (
                (value.inArray && !value.inSteps) ||
                dslName === 'ExpressionSubElementDefinition' ||
                dslName === 'ExpressionDefinition' ||
                dslName?.endsWith('Expression') ||
                stepName === 'otherwise' ||
                stepName === 'doFinally' ||
                stepName === 'resilience4jConfiguration' ||
                stepName === 'faultToleranceConfiguration' ||
                stepName === 'errorHandler' ||
                stepName === 'onWhen' || // https://github.com/apache/workflow-karavan/issues/1420
                stepName === 'deadLetterChannel' ||
                stepName === 'defaultErrorHandler' ||
                stepName === 'jtaTransactionErrorHandler' ||
                stepName === 'noErrorHandler' ||
                stepName === 'springTransactionErrorHandler' ||
                stepName === 'redeliveryPolicy' ||
                stepName === 'securityDefinitions' ||
                stepName === 'apiKey' ||
                stepName === 'basicAuth' ||
                stepName === 'bearer' ||
                stepName === 'mutualTls' ||
                stepName === 'oauth2' ||
                stepName === 'openIdConnect' ||
                stepName === 'openApi' ||
                key === 'from'
            ) {
                delete newValue.inArray;
                delete newValue.inSteps;
                return newValue;
            } else if (isKamelet && dslName === 'RouteDefinition') {
                delete value?.dslName;
                delete value?.stepName;
                return value;
            } else {
                delete newValue.inArray;
                delete newValue.inSteps;
                const xValue: any = {};
                xValue[stepName] = newValue;
                return xValue;
            }
        } else {
            if (value?.dslName === 'YAMLDataFormat') { // YAMLDataFormat constructor field
                value.constructor = value._constructor;
                delete value._constructor;
            }
            delete value?.dslName;
            return value;
        }
    };

    static yamlToIntegration = (filename: string, text: string): Integration => {
        const integration: Integration = Integration.createNew(filename);
        const fromYaml: any = yaml.load(text);
        const normalized: any = WorkflowUtil.normalizeObject(fromYaml);
        if (normalized?.apiVersion && normalized.apiVersion.startsWith('workflow.apache.org') && normalized.kind) {
            if (normalized?.metadata) {
                integration.metadata = normalized?.metadata;
            }
            if (normalized?.spec) {
                integration.spec.definition = normalized?.spec.definition;
                integration.spec.dependencies = normalized?.spec.dependencies;
                integration.spec.types = normalized?.spec.types;
            }
            const int: Integration = new Integration({ ...normalized });
            if (normalized.kind === 'Integration') {
                integration.type = 'crd';
                integration.spec.flows?.push(...WorkflowDefinitionYaml.flowsToWorkflowElements(int.spec.flows || []));
            } 
        } else if (Array.isArray(normalized)) {
            integration.type = 'plain';
            const flows: any[] = normalized;
            integration.spec.flows?.push(...WorkflowDefinitionYaml.flowsToWorkflowElements(flows));
        }
        return integration;
    };

    static yamlIsIntegration = (text: string): 'crd' | 'plain' | 'kamelet' | 'none' => {
        try {
            const fromYaml: any = yaml.load(text);
            const normalized: any = WorkflowUtil.normalizeObject(fromYaml);
            if (normalized?.apiVersion && normalized.apiVersion.startsWith('workflow.apache.org') && normalized.kind) {
                if (normalized.kind === 'Integration') {
                    return 'crd';
                } else if (normalized.kind === 'Kamelet') {
                    return 'kamelet';
                }
            } else if (Array.isArray(normalized)) {
                return 'plain';
            } else {
                return 'none';
            }
        } catch (e) {
            console.error(e);
        }
        return 'none';
    };
    static flowsToWorkflowElements = (flows: any[]): any[] => {
        const result: any[] = [];
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'restConfiguration'))
            .forEach((f: any) => result.push(WorkflowDefinitionYamlStep.readRestConfigurationDefinition(f.restConfiguration)));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'rest'))
            .forEach((f: any) => result.push(WorkflowDefinitionYamlStep.readRestDefinition(f.rest)));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'routeTemplate'))
            .forEach((f: any) => result.push(WorkflowDefinitionYamlStep.readRouteTemplateDefinition(f.routeTemplate)));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'route'))
            .forEach((f: any) => result.push(WorkflowDefinitionYamlStep.readRouteDefinition(f.route)));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'from'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteDefinition(new RouteDefinition({from: f.from}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'routeConfiguration'))
            .forEach((e: any) => result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(e.routeConfiguration)));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'errorHandler'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({errorHandler: f.errorHandler}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'onException'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({onException: f.onException}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'intercept'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({intercept: f.intercept}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'interceptFrom'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({interceptFrom: f.interceptFrom}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'interceptSendToEndpoint'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({interceptSendToEndpoint: f.interceptSendToEndpoint}))));
        flows.filter((e: any) => Object.prototype.hasOwnProperty.call(e, 'onCompletion'))
            .forEach((f: any) =>  result.push(WorkflowDefinitionYamlStep.readRouteConfigurationDefinition(new RouteConfigurationDefinition({onCompletion: f.onCompletion}))));

        return result;
    };


    // convert map style to properties if requires
    static flatMapProperty = (key: string, value: any, properties: Map<string, any>): Map<string, any> => {
        if (value === undefined) {
            return properties;
        }

        if (typeof value === 'object') {
            for (const k in value) {
                const key2 = key + '.' + k;
                const value2: any = value[k];
                WorkflowDefinitionYaml.flatMapProperty(key2, value2, new Map<string, any>()).forEach((value1, key1) =>
                    properties.set(key1, value1),
                );
            }
        } else {
            properties.set(key, value);
        }
        return properties;
    };

    // add generated Integration YAML into existing Integration YAML
    static addYamlToIntegrationYaml = (
        filename: string,
        workflowYaml: string | undefined,
        restYaml: string,
        addREST: boolean,
        addRoutes: boolean,
    ): string => {
        const existing =
            workflowYaml !== undefined
                ? WorkflowDefinitionYaml.yamlToIntegration(filename, workflowYaml)
                : Integration.createNew(filename);
        const generated = WorkflowDefinitionYaml.yamlToIntegration(filename, restYaml);

        const flows: WorkflowElement[] =
            existing.spec.flows?.filter(f => !['RouteDefinition', 'RestDefinition'].includes(f.dslName)) || [];

        const restE: WorkflowElement[] = existing.spec.flows?.filter(f => f.dslName === 'RestDefinition') || [];
        const restG: WorkflowElement[] = generated.spec.flows?.filter(f => f.dslName === 'RestDefinition') || [];

        if (addREST) {
            flows.push(...restG);
        } else {
            flows.push(...restE);
        }

        const routeE: WorkflowElement[] = existing.spec.flows?.filter(f => f.dslName === 'RouteDefinition') || [];
        const routeG: WorkflowElement[] = generated.spec.flows?.filter(f => f.dslName === 'RouteDefinition') || [];

        if (addRoutes) {
            flows.push(...routeG);
        } else {
            flows.push(...routeE);
        }

        existing.spec.flows = flows;
        return WorkflowDefinitionYaml.integrationToYaml(existing);
    };
}