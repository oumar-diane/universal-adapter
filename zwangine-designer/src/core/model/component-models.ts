export class ComponentTitle {
    kind: string = '';
    name: string = '';
    title: string = '';
    description: string = '';
    deprecated: boolean = false;
    firstVersion: string = '';
    label: string = '';
    javaType: string = '';
    supportLevel: string = '';
    supportType: string = '';
    groupId: string = '';
    artifactId: string = '';
    version: string = '';
    scheme: string = '';
    extendsScheme: string = '';
    syntax: string = '';
    async: boolean = false;
    api: boolean = false;
    consumerOnly: boolean = false;
    producerOnly: boolean = false;
    lenientProperties: boolean = false;
    componentProperties: any;
    remote: boolean = false;

    public constructor(init?: Partial<ComponentTitle>) {
        Object.assign(this, init);
    }
}

export class ComponentHeader {
    name: string = '';
    index: number = 0;
    kind: string = '';
    displayName: string = '';
    group: string = '';
    label: boolean = false;
    javaType: string = '';
    deprecated: boolean = false;
    deprecationNote: string = '';
    defaultValue: string = '';
    secret: boolean = false;
    autowired: boolean = false;
    description: string = '';
    constantName: string = '';

    public constructor(init?: Partial<ComponentHeader>) {
        Object.assign(this, init);
    }
}

export class Component {
    component: ComponentTitle = new ComponentTitle();
    properties: any;
    headers: any;

    public constructor(init?: Partial<Component>) {
        Object.assign(this, init);
    }
}

export class ComponentProperty {
    name: string = '';
    deprecated: boolean = false;
    description: string = '';
    displayName: string = '';
    group: string = '';
    kind: string = '';
    label: string = '';
    type: string = '';
    secret: boolean = false;
    enum: string[] = [];
    required: boolean = false;
    defaultValue: string | number | boolean | any;
    value: string | any;

    public constructor(init?: Partial<ComponentProperty>) {
        Object.assign(this, init);
    }
}

export class SupportedComponent {
    name: string = '';
    level: string = '';
    native: boolean = false;

    public constructor(init?: Partial<SupportedComponent>) {
        Object.assign(this, init);
    }
}