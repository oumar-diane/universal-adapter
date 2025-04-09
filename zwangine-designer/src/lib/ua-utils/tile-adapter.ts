import {ITile} from "@/components/catalog";
import {IComponentDefinition} from "@/core/model/catalog/components-catalog.ts";
import {CatalogKind} from "@/core";
import {IProcessorDefinition} from "@/core/model/catalog/processors-catalog.ts";

export const componentToTile = (componentDef: IComponentDefinition): ITile => {
    const { name, title, description, supportLevel, label, version } = componentDef.component;
    const headerTags: string[] = ['Component'];
    const tags: string[] = [];

    if (supportLevel && !componentDef.component.deprecated) {
        headerTags.push(supportLevel);
    } else {
        headerTags.push('Deprecated');
    }
    if (label) {
        tags.push(...label.split(','));
    }
    if (componentDef.component.consumerOnly) {
        tags.push('consumerOnly');
    }
    if (componentDef.component.producerOnly) {
        tags.push('producerOnly');
    }

    return {
        type: CatalogKind.Component,
        name,
        title,
        description,
        headerTags,
        tags,
        version,
    };
};

export const processorToTile = (processorDef: IProcessorDefinition): ITile => {
    const { name, title, description, label } = processorDef.model;
    const tags = label.split(',');

    return {
        type: CatalogKind.Processor,
        name,
        title,
        description,
        headerTags: ['Processor'],
        tags,
    };
};

export const entityToTile = (processorDef: IProcessorDefinition): ITile => {
    const entityTile = processorToTile(processorDef);
    entityTile.type = CatalogKind.Entity;
    entityTile.headerTags = ['Entity'];

    return entityTile;
};


