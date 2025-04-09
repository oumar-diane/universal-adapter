import { YamlDsl, WorkflowDefinition } from "@/@types";
import { createPropertiesSorter, isDefined } from "@/lib/utility";
import { TileFilter } from "@/components/catalog";
import { WorkflowVisualEntity } from "../entity/workflow-visual-entity.ts";
import { WorkflowConfigurationVisualEntity } from "../entity/workflow-configuration-visual-entity.ts";
import { OnCompletionVisualEntity } from "../entity/on-completion-visual-entity.ts";
import { OnExceptionVisualEntity } from "../entity/on-exception-visual-entity.ts";
import { RestConfigurationVisualEntity } from "../entity/rest-configuration-visual-entity.ts";
import { RestVisualEntity } from "../entity/rest-visual-entity.ts";
import {BaseVisualEntityDefinition, Resource } from "../model/resource/resource.ts";
import {BaseEntity, EntityType } from "../model/entity/base-entity.ts";
import {AddStepMode, BaseVisualEntityConstructor} from "../model/entity/base-visual-entity.ts";
import {ResourceSerializer, SerializerType} from "../model/resource/resource-serializer.ts";
import {CatalogKind} from "../model/catalog/catalog-kind.ts";
import {SourceSchemaType} from "../model/schema/schema.ts";
import {WorkflowVisualEntityData} from "../model/component-type.ts";
import { YamlResourceSerializer } from "./yaml-resource-serializer.ts";
import {CatalogService} from "../catalog/catalog-service.ts";
import {FlowTemplatesService} from "../flow-templates-service.ts";
import {ComponentFilterService} from "../component-filter-service.ts";
import {NonVisualEntity} from "../entity/non-visual-entity.ts";

export class WorkflowResource implements Resource {
    static readonly SUPPORTED_ENTITIES: { type: EntityType; group: string; Entity: BaseVisualEntityConstructor }[] =
        [
            { type: EntityType.Workflow, group: '', Entity: WorkflowVisualEntity },
            { type: EntityType.WorkflowConfiguration, group: 'Configuration', Entity: WorkflowConfigurationVisualEntity },
            { type: EntityType.OnCompletion, group: 'Configuration', Entity: OnCompletionVisualEntity },
            { type: EntityType.OnException, group: 'Error Handling', Entity: OnExceptionVisualEntity },
            { type: EntityType.RestConfiguration, group: 'Rest', Entity: RestConfigurationVisualEntity },
            { type: EntityType.Rest, group: 'Rest', Entity: RestVisualEntity },
        ];
    static readonly PARAMETERS_ORDER = ['id', 'description', 'uri', 'parameters', 'steps'];
    private static readonly ENTITIES_ORDER_PRIORITY = [
        EntityType.OnException,
        EntityType.OnCompletion,
    ];
    readonly sortFn = createPropertiesSorter(WorkflowResource.PARAMETERS_ORDER) as (
        a: unknown,
        b: unknown,
    ) => number;
    private entities: BaseEntity[] = [];
    private resolvedEntities: BaseVisualEntityDefinition | undefined;

    constructor(
        rawEntities?: YamlDsl,
        private serializer: ResourceSerializer = new YamlResourceSerializer(),
    ) {
        if (!rawEntities) return;

        const entities = Array.isArray(rawEntities) ? rawEntities : [rawEntities];
        this.entities = entities.reduce((acc, rawItem) => {
            const entity = this.getEntity(rawItem);
            if (isDefined(entity) && typeof entity === 'object') {
                acc.push(entity);
            }
            return acc;
        }, [] as BaseEntity[]);
    }

    getCanvasEntityList(): BaseVisualEntityDefinition {
        if (isDefined(this.resolvedEntities)) {
            return this.resolvedEntities;
        }

        this.resolvedEntities = WorkflowResource.SUPPORTED_ENTITIES.reduce(
            (acc, { type, group }) => {
                const catalogEntity = CatalogService.getComponent(CatalogKind.Entity, type);
                const entityDefinition = {
                    name: type,
                    title: catalogEntity?.model.title || type,
                    description: catalogEntity?.model.description || '',
                };

                if (group === '') {
                    acc.common.push(entityDefinition);
                    return acc;
                }

                acc.groups[group] ??= [];
                acc.groups[group].push(entityDefinition);
                return acc;
            },
            { common: [], groups: {} } as BaseVisualEntityDefinition,
        );

        return this.resolvedEntities;
    }

    getSerializerType() {
        return this.serializer.getType();
    }

    setSerializer(serializerType: SerializerType): void {
        // Preserve comments
        const serializer = serializerType === 'YAML' ?  new YamlResourceSerializer() : new YamlResourceSerializer();

        serializer.setComments(this.serializer.getComments());
        serializer.setMetadata(this.serializer.getMetadata());

        this.serializer = serializer;
    }

    addNewEntity(entityType?: EntityType): string {
        if (entityType && entityType !== EntityType.Workflow) {
            const supportedEntity = WorkflowResource.SUPPORTED_ENTITIES.find(({ type }) => type === entityType);
            if (supportedEntity) {
                const entity = new supportedEntity.Entity();

                /** Error related entities should be added at the beginning of the list */
                if (WorkflowResource.ENTITIES_ORDER_PRIORITY.includes(entityType)) {
                    this.entities.unshift(entity);
                } else {
                    this.entities.push(entity);
                }
                return entity.id;
            }
        }

        const template = FlowTemplatesService.getFlowTemplate(this.getType());
        const workflow = template[0] as WorkflowDefinition;
        const entity = new WorkflowVisualEntity(workflow);
        this.entities.push(entity);
        console.log("adding entity: ", template);

        return entity.id;
    }

    getType(): SourceSchemaType {
        return SourceSchemaType.Workflow;
    }

    supportsMultipleVisualEntities(): boolean {
        return true;
    }

    getVisualEntities(): WorkflowVisualEntity[] {
        return this.entities.filter(
            (entity) =>
                entity instanceof WorkflowVisualEntity ||
                WorkflowResource.SUPPORTED_ENTITIES.some(({ Entity }) => entity instanceof Entity),
        ) as WorkflowVisualEntity[];
    }

    getEntities(): BaseEntity[] {
        return this.entities.filter((entity) => !(entity instanceof WorkflowVisualEntity)) as BaseEntity[];
    }

    toJSON(): unknown {
        return this.entities.map((entity) => entity.toJSON());
    }

    toString() {
        return this.serializer.serialize(this);
    }


    removeEntity(id?: string): void {
        if (!isDefined(id)) return;
        const index: number = this.entities.findIndex((e) => e.id === id);

        if (index !== -1) {
            this.entities.splice(index, 1);
        }
    }

    /** Components Catalog related methods */
    getCompatibleComponents(
        mode: AddStepMode,
        visualEntityData: WorkflowVisualEntityData,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        definition?: any,
    ): TileFilter {
        return ComponentFilterService.getCompatibleComponents(mode, visualEntityData, definition);
    }

    private getEntity(rawItem: unknown): BaseEntity | undefined {
        if (!isDefined(rawItem) || Array.isArray(rawItem)) {
            return undefined;
        }

        for (const { Entity } of WorkflowResource.SUPPORTED_ENTITIES) {
            if (Entity.isApplicable(rawItem)) {
                return new Entity(rawItem);
            }
        }

        return new NonVisualEntity(rawItem as string);
    }
}
