
import {
    WorkflowElement, Integration,
    IntegrationFile,
} from '../model/integration-defintion';
import { WorkflowDefinitionYaml } from './workflow-definition-yaml.ts';
import { FromDefinition} from '../model/workflow-definition';
import { WorkflowDefinitionApiExt } from './workflow-definition-api-ext';

const sendReceiveDSL: string[] =
    ['ToDefinition', 'FromDefinition', 'ToDynamicDefinition', 'PollEnrichDefinition',
        'EnrichDefinition', 'WireTapDefinition', 'UnmarshalDefinition', 'MarshalDefinition'];

export const GLOBAL = 'global:';
export const  ROUTE = 'route:';


export class VariableUtil {
    private constructor() {
    }

    static findVariables = (files: IntegrationFile[]): string[] => {
        const integrations = files.filter(file => file.name?.endsWith(".yaml"))
            .map(file => WorkflowDefinitionYaml.yamlToIntegration(file.name, file.code));
        return VariableUtil.findVariablesInIntegrations(integrations);
    };

    static findVariablesInIntegrations = (integrations: Integration[]): string[] => {
        const result: string[] = []
        integrations.forEach(i => {
            const routes = i.spec.flows?.filter(flow => flow.dslName === 'RouteDefinition');
            routes?.forEach(route => {
                const from: FromDefinition = route.from;
                VariableUtil.findVariablesInStep(from, result);
            })

        })
        return VariableUtil.sortVariables(result);
    };

    static sortVariables = (variables: string[]): string [] => {
        const global = [...new Set(variables.filter(v => v && v.startsWith(GLOBAL)))].sort();
        const route = [...new Set(variables.filter(v => v && v.startsWith(ROUTE)))].sort();
        const exchange = [...new Set(variables.filter(v => v && !v.startsWith(ROUTE) && !v.startsWith(GLOBAL)))].sort();
        return global.concat(route, exchange);
    }

    static findVariablesInStep = (step: WorkflowElement, result: string[]) => {
        if (step !== undefined) {
            const el = (step as any);
            if (sendReceiveDSL.includes(el.dslName)) {
                VariableUtil.findVariablesInProps(el, 'variableSend', result);
                VariableUtil.findVariablesInProps(el, 'variableReceive', result);
            } else if (el.dslName === 'ConvertVariableDefinition') {
                VariableUtil.findVariablesInProps(el, 'name', result);
                VariableUtil.findVariablesInProps(el, 'toName', result);
            } else if (el.dslName === 'SetVariableDefinition') {
                VariableUtil.findVariablesInProps(el, 'name', result);
            } else if (el.dslName === 'RemoveVariableDefinition') {
                VariableUtil.findVariablesInProps(el, 'name', result);
            }
            // check children elements
            const childElements = WorkflowDefinitionApiExt.getElementChildrenDefinition(el.dslName);
            childElements.forEach(child => {
                if (child.multiple) {
                    const sub = (el[child.name] as WorkflowElement[]);
                    VariableUtil.findVariablesInSteps(sub, result);
                } else {
                    const sub = (el[child.name] as WorkflowElement);
                    VariableUtil.findVariablesInStep(sub, result);
                }
            })
        }
    }

    static findVariablesInSteps = (steps: WorkflowElement[], result: string[]) => {
        if (steps !== undefined && steps.length > 0) {
            steps.forEach(step => VariableUtil.findVariablesInStep(step, result))
        }
    }

    static findVariablesInProps = (step: WorkflowElement, propertyName: string, result: string[]) => {
        const el = (step as any);
        if (Object.prototype.hasOwnProperty.call(el, propertyName)) {
            result.push(el[propertyName])
        }
    }
}