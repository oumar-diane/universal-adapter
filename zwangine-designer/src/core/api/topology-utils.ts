
import {
    DeleteDefinition,
    FromDefinition,
    GetDefinition,
    HeadDefinition,
    PatchDefinition,
    PostDefinition,
    PutDefinition,
    RestDefinition, RouteConfigurationDefinition, RouteDefinition, SagaDefinition,
} from '../model/workflow-definition';
import {
    WorkflowElement,
    Integration,
} from '../model/integration-defintion';
import {
    TopologyIncomingNode,
    TopologyOutgoingNode,
    TopologyRestNode, TopologyRouteConfigurationNode,
    TopologyRouteNode,
} from '../model/topology-definition';
import { ComponentApi, INTERNAL_COMPONENTS } from './component-api';
import { WorkflowDefinitionApiExt } from './workflow-definition-api-ext';
import { WorkflowDisplayUtil } from './workflow-display-util';
import { WorkflowUtil } from './workflow-util';

const outgoingDefinitions: string[] = ['ToDefinition', 'ToDynamicDefinition', 'PollEnrichDefinition', 'EnrichDefinition', 'WireTapDefinition', 'SagaDefinition', 'PollDefinition'];

export class ChildElement {
    constructor(public name: string = '', public className: string = '', public multiple: boolean = false) {
    }
}

export class TopologyUtils {
    private constructor() {
    }

    static getOutgoingDefinitions = (): string[] => {
        return outgoingDefinitions;
    };

    static isElementInternalComponent = (element: WorkflowElement): boolean => {
        const uri = (element as any).uri;
        const component = ComponentApi.findByName(uri);
        if (INTERNAL_COMPONENTS.includes(uri?.split(':')?.[0])) return true;
        return component !== undefined && component.component.remote !== true;
    };

    static getConnectorType = (element: WorkflowElement): 'component' | 'container' => {
        return WorkflowUtil.isContainer(element) ? 'container' : 'component';
    };

    static cutKameletUriSuffix = (uri: string): string => {
        if (uri.endsWith('-sink')) {
            return uri.substring(0, uri.length - 5);
        } else if (uri.endsWith('-source')) {
            return uri.substring(0, uri.length - 7);
        } else if (uri.endsWith('-action')) {
            return uri.substring(0, uri.length - 7);
        } else {
            return uri;
        }
    };

    static getUniqueUri = (element: WorkflowElement): string => {
        const uri: string = (element as any).uri || '';
        let result = uri.startsWith('kamelet') ? TopologyUtils.cutKameletUriSuffix(uri).concat(':') : uri.concat(':');
        const className = element.dslName;
        if (className === 'FromDefinition' || className === 'ToDefinition') {
            const requiredProperties = WorkflowUtil.getComponentProperties(element).filter(p => p.required);
            for (const property of requiredProperties) {
                const value = WorkflowDefinitionApiExt.getParametersValue(element, property.name, property.kind === 'path');
                if (value !== undefined && property.type === 'string' && value.trim().length > 0) {
                    result = result + property.name + '=' + value + '&';
                }
            }
        }
        return result.endsWith('&') ? result.substring(0, result.length - 1) : result;
    };

    static hasDirectUri = (element: WorkflowElement): boolean => {
        return this.hasUriStartWith(element, 'direct');
    };

    static hasSedaUri = (element: WorkflowElement): boolean => {
        return this.hasUriStartWith(element, 'seda');
    };

    static hasUriStartWith = (element: WorkflowElement, text: string): boolean => {
        if ((element as any).uri && typeof (element as any).uri === 'string') {
            return (element as any).uri.startsWith(text);
        } else if (element.dslName === 'SagaDefinition') {
            const completion = (element as SagaDefinition).completion || '';
            const compensation = (element as SagaDefinition).compensation || '';
            return completion.startsWith(text) || compensation.startsWith(text);
        } else {
            return false;
        }
    };

    static findTopologyRestNodes = (integration: Integration[]): TopologyRestNode[] => {
        const result: TopologyRestNode[] = [];
        integration.forEach(i => {
            try {
                const filename = i.metadata.name;
                const routes = i.spec.flows?.filter(flow => flow.dslName === 'RestDefinition');
                routes?.forEach((rest: RestDefinition) => {
                    const uris: string[] = [];
                    rest?.get?.forEach((d: GetDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    rest?.post?.forEach((d: PostDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    rest?.put?.forEach((d: PutDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    rest?.delete?.forEach((d: DeleteDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    rest?.patch?.forEach((d: PatchDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    rest?.head?.forEach((d: HeadDefinition) => {
                        if (d.to) uris.push(d.to);
                    });
                    const title = '' + (rest.description ? rest.description : rest.id);
                    result.push(new TopologyRestNode(rest.path || '', '' + rest.id, uris, title, filename, rest));
                });
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findTopologyIncomingNodes = (integration: Integration[]): TopologyIncomingNode[] => {
        const result: TopologyIncomingNode[] = [];
        integration.forEach(i => {
            try {
                const filename = i.metadata.name;
                const routes = i.spec.flows?.filter(flow => flow.dslName === 'RouteDefinition');
                const routeElements = routes?.map(r => {
                    const id = 'incoming-' + r.id;
                    const title = WorkflowDisplayUtil.getStepDescription(r.from);
                    const type = TopologyUtils.isElementInternalComponent(r.from) ? 'internal' : 'external';
                    const connectorType = TopologyUtils.getConnectorType(r.from);
                    const uniqueUri = TopologyUtils.getUniqueUri(r.from);
                    return new TopologyIncomingNode(id, type, connectorType, r.id, title, filename, r.from, uniqueUri);
                }) || [];
                result.push(...routeElements);
                const templates = i.spec.flows?.filter(flow => flow.dslName === 'RouteTemplateDefinition');
                const templateElements = templates?.map(t => {
                    const r = t.route;
                    const id = 'incoming-' + r.id;
                    const title = WorkflowDisplayUtil.getStepDescription(r.from);
                    const type = TopologyUtils.isElementInternalComponent(r.from) ? 'internal' : 'external';
                    const connectorType = TopologyUtils.getConnectorType(r.from);
                    const uniqueUri = TopologyUtils.getUniqueUri(r.from);
                    return new TopologyIncomingNode(id, type, connectorType, r.id, title, filename, r.from, uniqueUri);
                }) || [];
                result.push(...templateElements);
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findTopologyRouteNodes = (integration: Integration[]): TopologyRouteNode[] => {
        const result: TopologyRouteNode[] = [];
        integration.forEach(i => {
            try {
                const filename = i.metadata.name;
                const routes = i.spec.flows?.filter(flow => flow.dslName === 'RouteDefinition');
                const routeElements = routes?.map(r => {
                    const id = 'route-' + r.id;
                    const title = '' + (r.description ? r.description : r.id);
                    return new TopologyRouteNode(id, r.id, title, filename, r.from, r);
                }) || [];
                result.push(...routeElements);
                const templates = i.spec.flows?.filter(flow => flow.dslName === 'RouteTemplateDefinition');
                const templateElements = templates?.map(t => {
                    const r = t.route;
                    const id = 'route-' + r.id;
                    const title = '' + (r.description ? r.description : r.id);
                    return new TopologyRouteNode(id, r.id, title, filename, r.from, r, t.id, t.description);
                }) || [];
                result.push(...templateElements);
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findTopologyRouteConfigurationNodes = (integration: Integration[]): TopologyRouteConfigurationNode[] => {
        const result: TopologyRouteConfigurationNode[] = [];
        integration.forEach(i => {
            try {
                const filename = i.metadata.name;
                const routes = i.spec.flows?.filter(flow => flow.dslName === 'RouteConfigurationDefinition');
                const routeElements = routes?.map(r => {
                    const id = 'route-' + r.id;
                    const title = '' + (r.description ? r.description : r.id);
                    return new TopologyRouteConfigurationNode(id, r.id, title, filename, r);
                }) || [];
                result.push(...routeElements);
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findTopologyRouteOutgoingNodes = (integrations: Integration[]): TopologyOutgoingNode[] => {
        const result: TopologyOutgoingNode[] = [];
        integrations.forEach(i => {
            try {
                const filename = i.metadata.name;
                const routes = i.spec.flows?.filter(flow => flow.dslName === 'RouteDefinition') || [];
                const routeFromTemplates = i.spec.flows?.filter(flow => flow.dslName === 'RouteTemplateDefinition').map(rt => rt.route) || [];
                routes.concat(routeFromTemplates).forEach(route => {
                    const from: FromDefinition = route.from;
                    const elements = TopologyUtils.findOutgoingInStep(from, []);
                    elements.forEach((e: any) => {
                        const id = 'outgoing-' + route.id + '-' + e.id;
                        const title = WorkflowDisplayUtil.getStepDescription(e);
                        const type = TopologyUtils.isElementInternalComponent(e) ? 'internal' : 'external';
                        const connectorType = TopologyUtils.getConnectorType(e);
                        const uniqueUri = TopologyUtils.getUniqueUri(e);
                        result.push(new TopologyOutgoingNode(id, type, connectorType, route.id, title, filename, e, uniqueUri));
                    });
                    result.push(...TopologyUtils.findDeadLetterChannelNodes(route, filename));
                });
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findDeadLetterChannelNodes(route: RouteDefinition, filename: string): TopologyOutgoingNode[] {
        const result: TopologyOutgoingNode[] = [];
        try {
            const deadLetterChannel = route.errorHandler?.deadLetterChannel;
            const deadLetterUri = deadLetterChannel?.deadLetterUri;
            if (deadLetterChannel !== undefined && deadLetterUri !== undefined) {
                const parts = deadLetterUri.split(':');
                if (parts.length > 1 && INTERNAL_COMPONENTS.includes(parts[0])) {
                    const id = 'outgoing-' + route.id + '-' + deadLetterChannel?.id;
                    const title = WorkflowDisplayUtil.getStepDescription(deadLetterChannel);
                    const type = 'internal';
                    const connectorType = 'component';
                    result.push(new TopologyOutgoingNode(id, type, connectorType, route.id || '', title, filename, deadLetterChannel, deadLetterUri));
                }
            }
        } catch (e) {
            console.error(e);
        }
        return result;
    }

    static findTopologyRouteConfigurationOutgoingNodes = (integrations: Integration[]): TopologyOutgoingNode[] => {
        const result: TopologyOutgoingNode[] = [];
        integrations.forEach(i => {
            try {
                const filename = i.metadata.name;
                const rcs = i.spec.flows?.filter(flow => flow.dslName === 'RouteConfigurationDefinition');
                rcs?.forEach((rc: RouteConfigurationDefinition) => {
                    const children: WorkflowElement[] = [];
                    children.push(...rc.intercept || []);
                    children.push(...rc.interceptFrom || []);
                    children.push(...rc.interceptSendToEndpoint || []);
                    children.push(...rc.onCompletion || []);
                    children.push(...rc.onException || []);
                    children.forEach(child => {
                        const elements = TopologyUtils.findOutgoingInStep(child, []);
                        elements.forEach((e: any) => {
                            const id = 'outgoing-' + rc.id + '-' + e.id;
                            const title = WorkflowDisplayUtil.getStepDescription(e);
                            const type = TopologyUtils.isElementInternalComponent(e) ? 'internal' : 'external';
                            const connectorType = TopologyUtils.getConnectorType(e);
                            const uniqueUri = TopologyUtils.getUniqueUri(e);
                            result.push(new TopologyOutgoingNode(id, type, connectorType, rc.id || 'undefined', title, filename, e, uniqueUri));
                        });
                    });
                    if (rc.errorHandler?.deadLetterChannel) {
                        const e = rc.errorHandler?.deadLetterChannel;
                        const id = 'outgoing-' + rc.id + '-' + e.id;
                        const title = WorkflowDisplayUtil.getStepDescription(e);
                        const comp = e?.deadLetterUri?.split(':')?.[0];
                        const type = INTERNAL_COMPONENTS.includes(comp) ? 'internal' : 'external';
                        const connectorType = 'component';
                        const uniqueUri = e?.deadLetterUri;
                        result.push(new TopologyOutgoingNode(id, type, connectorType, rc.id || 'undefined', title, filename, e, uniqueUri));
                    }
                });
            } catch (e) {
                console.error(e);
            }
        });
        return result;
    };

    static findOutgoingInStep = (step: WorkflowElement, result: WorkflowElement[]): WorkflowElement[] => {
        if (step !== undefined) {
            const el = (step as any);
            try {
                if (outgoingDefinitions.includes(el.dslName)) {
                    result.push(step);
                } else {
                    const childElements = WorkflowDefinitionApiExt.getElementChildrenDefinition(el.dslName);
                    childElements.forEach(child => {
                        if (child.multiple) {
                            const sub = (el[child.name] as WorkflowElement[]);
                            TopologyUtils.findOutgoingInSteps(sub, result);
                        } else {
                            const sub = (el[child.name] as WorkflowElement);
                            TopologyUtils.findOutgoingInStep(sub, result);
                        }
                    });
                }
            } catch (e) {
                console.error(e);
            }
        }
        return result;
    };

    static findOutgoingInSteps = (steps: WorkflowElement[], result: WorkflowElement[]): WorkflowElement[] => {
        if (steps !== undefined && steps.length > 0) {
            steps.forEach(step => TopologyUtils.findOutgoingInStep(step, result));
        }
        return result;
    };

    static getNodeIdByUriAndName(tins: TopologyIncomingNode[], uri: string, name: string): string | undefined {
        if (uri && name) {
            const node = tins
                .filter(r => r.from.uri === uri
                    && (r?.from?.parameters?.name === name || r?.from?.parameters?.address === name),
                ).at(0);
            if (node) {
                return node.id;
            }
        }
    }

    static getNodeIdByUri(tins: TopologyIncomingNode[], uri: string): string | undefined {
        const parts = uri.split(':');
        if (parts.length > 1) {
            return TopologyUtils.getNodeIdByUriAndName(tins, parts[0], parts[1]);
        }
    }

    static getRouteIdByUriAndName(tins: TopologyIncomingNode[], uri: string, name: string): string | undefined {
        if (uri && name) {
            const node = tins
                .filter(r => r.from.uri === uri
                    && (r?.from?.parameters?.name === name || r?.from?.parameters?.address === name),
                ).at(0);
            if (node) {
                return 'route-' + node.routeId;
            }
        }
    }

    static getNodeIdByUniqueUri(tins: TopologyIncomingNode[], uniqueUri: string): string [] {
        const result: string[] = [];
        tins.filter(r => r.uniqueUri === uniqueUri)
            ?.forEach(node => result.push(node.id));
        return result;
    }

    static getRouteIdByUri(tins: TopologyIncomingNode[], uri: string): string | undefined {
        const parts = uri.split(':');
        if (parts.length > 1) {
            return TopologyUtils.getRouteIdByUriAndName(tins, parts[0], parts[1]);
        }
    }
}