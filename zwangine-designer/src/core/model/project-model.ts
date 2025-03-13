import { v4 as uuidv4 } from 'uuid';

export class ProjectProperty {
    id: string = '';
    key: string = '';
    value: any;

    public constructor(init?: Partial<ProjectProperty>) {
        Object.assign(this, init);
    }

    static createNew(key: string, value: any): ProjectProperty {
        return new ProjectProperty({ id: uuidv4(), key: key, value: value });
    }
}

export class ProjectModel {
    properties: ProjectProperty[] = [];

    public constructor(init?: Partial<ProjectModel>) {
        Object.assign(this, init);
    }

    static createNew(init?: Partial<ProjectModel>): ProjectModel {
        return new ProjectModel(init ? init : {});
    }
}