import {TileFilter} from "@/components/catalog";
import {BaseEntity, EntityType} from "../entity/base-entity.ts";
import {AddStepMode, BaseVisualEntity, IVisualizationNodeData} from "../entity/base-visual-entity.ts";
import {SerializerType} from "./resource-serializer.ts";
import {SourceSchemaType} from "../schema/schema.ts";


export interface Resource {
    getVisualEntities(): BaseVisualEntity[];
    getEntities(): BaseEntity[];
    addNewEntity(entityType?: EntityType): string;
    removeEntity(id?: string): void;
    supportsMultipleVisualEntities(): boolean;
    toJSON(): unknown;
    toString(): string;
    getType(): SourceSchemaType;
    getCanvasEntityList(): BaseVisualEntityDefinition;
    getSerializerType(): SerializerType;
    setSerializer(serializer: SerializerType): void;

    /** Components Catalog related methods */
    getCompatibleComponents(
        mode: AddStepMode,
        visualEntityData: IVisualizationNodeData,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        definition?: any,
    ): TileFilter | undefined;

    sortFn?: (a: unknown, b: unknown) => number;
}

export interface BaseVisualEntityDefinition {
    common: BaseVisualEntityDefinitionItem[];
    groups: Record<string, BaseVisualEntityDefinitionItem[]>;
}

export interface BaseVisualEntityDefinitionItem {
    name: EntityType;
    title: string;
    description: string;
}

