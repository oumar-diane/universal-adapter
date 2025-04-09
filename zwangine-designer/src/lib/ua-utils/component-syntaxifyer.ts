import {CatalogKind, CatalogService} from "@/core";
import {getValue, setValue} from "@/lib/utility";

export function getSyntaxPathDelimiter(syntax:string, syntaxPathComponentName: string) {
    const componentIndex = syntax.indexOf(syntaxPathComponentName)
    if(componentIndex == -1) {
        return componentIndex
    }
    const position = componentIndex - 1 ;
    return syntax.charAt(position);
}
export function extractSyntax(str:string) {
    const withoutPrefix = str.split(':').slice(1).join(':');
    return withoutPrefix.split(/[:/]/);
}
export function applySyntaxToUri(catalogService: typeof CatalogService , originalUri: string, parsedSourceCode:any) {
    const component = catalogService.getComponent(CatalogKind.Component, originalUri)
    const syntax = getValue(component, "component.syntax");
    const componentName = getValue(component, "component.name");
    const syntaxEntities = extractSyntax(syntax);
    let syntaxUri = String(componentName)
    for(const syntaxEntity of syntaxEntities) {
        const paramDefaultValue = getValue(component, "properties."+syntaxEntity+".defaultValue");
        let entityValue = getValue(parsedSourceCode,"parameters."+syntaxEntity);
        if(entityValue == undefined) {
            entityValue = paramDefaultValue
        }
        syntaxUri += getSyntaxPathDelimiter(syntax, syntaxEntity) + `${entityValue}`;
    }
    setValue(parsedSourceCode, "uri", syntaxUri);
}

export function applyRuntimeSyntax(
    catalogService: typeof CatalogService,
    stepsDefinition: any,
) {
    for (const step of stepsDefinition) {
        const stepValue: any = Object.values(step)[0]
        console.log("child step: ", stepValue);
        const stepUri = getValue(stepValue, "uri");
        const isChildStep = getValue(stepValue, "steps");
        if (isChildStep) {
            applyRuntimeSyntax(catalogService, isChildStep);
        }
        if (stepUri == undefined) {
            continue;
        }
        applySyntaxToUri(catalogService, stepUri, stepValue);
    }
}