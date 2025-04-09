import { ProcessorDefinition } from "@/@types";
import {INodeMapper} from "../model/mapper/node-mapper.ts";
import {IElementLookupResult} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";

export class RootNodeMapper implements INodeMapper {
    private readonly mappers: Map<keyof ProcessorDefinition, INodeMapper> = new Map();
    private defaultMapper: INodeMapper | undefined;

    registerMapper(processorName: keyof ProcessorDefinition, mapper: INodeMapper): void {
        this.mappers.set(processorName, mapper);
    }

    registerDefaultMapper(mapper: INodeMapper): void {
        this.defaultMapper = mapper;
    }

    getVizNodeFromProcessor(
        path: string,
        componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        const mapper = this.mappers.get(componentLookup.processorName) || this.defaultMapper;

        if (!mapper) {
            throw new Error(`No mapper found for processor: ${componentLookup.processorName}`);
        }

        return mapper.getVizNodeFromProcessor(path, componentLookup, entityDefinition);
    }
}
