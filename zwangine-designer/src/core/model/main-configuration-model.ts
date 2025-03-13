
export class ApplicationProperty {
    name: string = '';
    value?: string;
    defaultValue?: string;
    description?: string;
    type?: string;

    public constructor(init?: Partial<ApplicationProperty>) {
        Object.assign(this, init);
    }
}

export class ApplicationPropertyGroup {
    name: string = '';
    description?: string;

    public constructor(init?: Partial<ApplicationPropertyGroup>) {
        Object.assign(this, init);
    }
}