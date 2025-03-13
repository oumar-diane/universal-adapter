
import { WorkflowElement } from './integration-defintion';
import { FromDefinition, RestDefinition, RouteConfigurationDefinition, RouteDefinition } from './workflow-definition';

export class TopologyRestNode {
    path: string;
    id: string;
    uris: string[];
    title: string;
    fileName: string;
    rest: RestDefinition;

    constructor(path: string, id: string, uris: string[], title: string, fileName: string, rest: RestDefinition) {
        this.path = path;
        this.id = id;
        this.uris = uris;
        this.title = title;
        this.fileName = fileName;
        this.rest = rest;
    }
}

export class TopologyIncomingNode {
    id: string;
    type: 'internal' | 'external';
    connectorType: 'component' | 'container';
    routeId: string;
    title: string;
    fileName: string;
    from: FromDefinition;
    uniqueUri?: string;


    constructor(id: string, type: "internal" | "external", connectorType: "component" | "container", routeId: string, title: string, fileName: string, from: FromDefinition, uniqueUri: string) {
        this.id = id;
        this.type = type;
        this.connectorType = connectorType;
        this.routeId = routeId;
        this.title = title;
        this.fileName = fileName;
        this.from = from;
        this.uniqueUri = uniqueUri;
    }
}

export class TopologyRouteNode {
    id: string;
    routeId: string;
    title: string;
    fileName: string;
    from: FromDefinition;
    route: RouteDefinition
    templateId?: string
    templateTitle?: string

    constructor(id: string, routeId: string, title: string, fileName: string, from: FromDefinition, route: RouteDefinition, templateId?: string, templateTitle?: string) {
        this.id = id;
        this.routeId = routeId;
        this.title = title;
        this.fileName = fileName;
        this.from = from;
        this.route = route;
        this.templateId = templateId;
        this.templateTitle = templateTitle;
    }
}

export class TopologyRouteConfigurationNode {
    id: string;
    routeConfigurationId: string;
    title: string;
    fileName: string;
    routeConfiguration: RouteConfigurationDefinition

    constructor(id: string, routeConfigurationId: string, title: string, fileName: string, routeConfiguration: RouteConfigurationDefinition) {
        this.id = id;
        this.routeConfigurationId = routeConfigurationId;
        this.title = title;
        this.fileName = fileName;
        this.routeConfiguration = routeConfiguration;
    }
}

export class TopologyOutgoingNode {
    id: string;
    type: 'internal' | 'external';
    connectorType: 'component' | 'container';
    routeId: string;
    title: string;
    fileName: string;
    step: WorkflowElement;
    uniqueUri?: string;


    constructor(id: string, type: "internal" | "external", connectorType: "component" | "container", routeId: string, title: string, fileName: string, step: WorkflowElement, uniqueUri: string) {
        this.id = id;
        this.type = type;
        this.connectorType = connectorType;
        this.routeId = routeId;
        this.title = title;
        this.fileName = fileName;
        this.step = step;
        this.uniqueUri = uniqueUri;
    }
}