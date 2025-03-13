
const Templates: Map<string, string> = new Map<string, string>();
const JavaCode: Map<string, string> = new Map<string, string>();

export class TemplateApi {
    private constructor() {}

    static saveTemplates = (templates: Map<string, string>, clean: boolean = false): void => {
        if (clean) Templates.clear();
        templates.forEach((value, key) => Templates.set(key, value));
    };

    static saveTemplate = (name: string, code: string): void => {
        Templates.set(name, code);
    };

    static getTemplate = (name: string): string | undefined => {
        return Templates.get(name);
    };

    static generateCode = (name: string, beanName: string): string | undefined => {
        const template: string | undefined = TemplateApi.getTemplate(name);
        if (template) {
            return template.replace('${NAME}', beanName);
        } else {
            throw new Error('Template not found');
        }
    };

    static saveJavaCodes = (javaCode: Map<string, string>, clean: boolean = false): void => {
        if (clean) JavaCode.clear();
        javaCode.forEach((value, key) => JavaCode.set(key, value));
    };

    static saveJavaCode = (name: string, code: string): void => {
        JavaCode.set(name, code);
    };

    static getJavaCode = (name: string): string | undefined => {
        return JavaCode.get(name);
    };
}