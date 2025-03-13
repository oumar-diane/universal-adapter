import { v4 as uuidv4 } from 'uuid';

export class IntegrationFile {
    name: string = '';
    code: string = '';

    constructor(name: string, code: string) {
        this.name = name;
        this.code = code;
    }
}

export class DefinitionProperty {
    title: string = '';
    description: string = '';
    type: 'string' | 'integer' | 'boolean' = 'string';
    default?: any;
    example?: any;
    format?: string;
    "x-descriptors"?: string[];
    enum?: string[];

    public constructor(init?: Partial<DefinitionProperty>) {
        Object.assign(this, init);
    }
}

export class MediaType {
    mediaType: string = '';

    public constructor(init?: Partial<MediaType>) {
        Object.assign(this, init);
    }
}

export class Types {
    in?: MediaType = new MediaType();
    out?: MediaType = new MediaType();

    public constructor(init?: Partial<Types>) {
        Object.assign(this, init);
    }
}

export class Definition {
    title: string = '';
    description: string = '';
    required: string[] = [];
    type: string = 'object';
    properties: any = {};

    public constructor(init?: Partial<Definition>) {
        Object.assign(this, init);
    }
}

export class Spec {
    definition?: Definition;
    types?: Types;
    flows?: any[] = [];
    template?: any;
    dependencies?: string[];

    public constructor(init?: Partial<Spec>) {
        Object.assign(this, init);
    }
}

export class Metadata {
    name: string = '';

    public constructor(init?: Partial<Metadata>) {
        Object.assign(this, init);
    }
}

export class Integration {
    apiVersion: string = 'zwangine.zenithblox.org/v1';
    kind: string = 'Integration';
    metadata: Metadata = new Metadata();
    spec: Spec = new Spec();
    type: 'crd' | 'plain' | 'kamelet' = 'plain';

    public constructor(init?: Partial<Integration>) {
        Object.assign(this, init);
    }

    static createNew(name?: string, type: 'crd' | 'plain' = 'plain'): Integration {
        const i = new Integration({ type: type,
            metadata: new Metadata({ name: name }),
            kind : 'Integration',
            spec: new Spec({ flows: [] }) });

        return i;
    }
}

export class WorkflowElement {
    uuid: string = '';
    dslName: string = '';
    showChildren: boolean = true;

    constructor(dslName: string) {
        this.uuid = uuidv4();
        this.dslName = dslName;
    }

    hasId(): boolean {
        return Object.prototype.hasOwnProperty.call(this, 'id');
    }

    hasSteps(): boolean {
        return Object.prototype.hasOwnProperty.call(this, 'steps');
    }

    hasStepName(): boolean {
        return Object.prototype.hasOwnProperty.call(this, 'stepName');
    }
}

export class WorkflowElementMeta {
    step?: WorkflowElement;
    parentUuid?: string;
    position: number = 0;

    constructor(step?: WorkflowElement, parentUuid?: string, position: number = 0) {
        this.step = step;
        this.parentUuid = parentUuid;
        this.position = position;
    }
}
