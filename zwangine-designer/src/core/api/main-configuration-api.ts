
import { ApplicationProperty, ApplicationPropertyGroup } from '../model/main-configuration-model';

const MainApplicationProperties: ApplicationProperty[] = [];
const MainApplicationGroups: ApplicationPropertyGroup[] = [];

export class MainConfigurationApi {
    private constructor() {}


    static saveApplicationProperties = (objects: [], clean: boolean = false): void => {
        if (clean) MainApplicationProperties.length = 0;
        const properties: ApplicationProperty[] = objects.map(object => new ApplicationProperty(object));
        MainApplicationProperties.push(...properties);
    };

    static getApplicationProperties = (): ApplicationProperty[] => {
        const comps: ApplicationProperty[] = [];
        comps.push(...MainApplicationProperties);
        return comps;
    };

    static findByName = (name: string): ApplicationProperty | undefined => {
        return MainConfigurationApi.getApplicationProperties().find((c: ApplicationProperty) => c.name === name);
    };

    static saveApplicationPropertyGroups = (objects: []): void => {
        MainApplicationGroups.length = 0;
        const properties: ApplicationPropertyGroup[] = objects.map(object => new ApplicationPropertyGroup(object));
        MainApplicationProperties.push(...properties);
    };
}