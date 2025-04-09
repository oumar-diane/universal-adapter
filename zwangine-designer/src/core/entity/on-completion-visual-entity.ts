import {OnCompletion, ProcessorDefinition} from "@/@types";
import { getRandomId } from "@/lib/ua-utils";
import {isDefined, NodeIconResolver, NodeIconType} from "@/lib/utility";
import { ComponentSchemaService } from "../schema/component-schema-service.ts";
import {ModelValidationService} from "../model-validation-service.ts";
import {NodeMapperService} from "../mapper/node-mapper-service.ts";
import {
    BaseVisualEntity,
    IVisualizationNode,
    IVisualizationNodeData,
    NodeInteraction
} from "../model/entity/base-visual-entity.ts";
import { EntityType } from "../model/entity/base-entity.ts";
import {WorkflowVisualEntityData} from "../model/component-type.ts";
import {AbstractVisualEntity} from "./abstract-visual-entity.ts";


export class OnCompletionVisualEntity
    extends AbstractVisualEntity<{ onCompletion: OnCompletion }>
    implements BaseVisualEntity
{
    id: string;
    readonly type = EntityType.OnCompletion;
    static readonly ROOT_PATH = 'onCompletion';

    constructor(public onCompletionDef: { onCompletion: OnCompletion } = { onCompletion: {} }) {
        super(onCompletionDef);
        const id = onCompletionDef.onCompletion.id ?? getRandomId(OnCompletionVisualEntity.ROOT_PATH);
        this.id = id;
        this.onCompletionDef.onCompletion.id = id;
    }

    static isApplicable(onCompletionDef: unknown): onCompletionDef is { onCompletion: OnCompletion } {
        if (!isDefined(onCompletionDef) || Array.isArray(onCompletionDef) || typeof onCompletionDef !== 'object') {
            return false;
        }

        const objectKeys = Object.keys(onCompletionDef!);

        return (
            objectKeys.length === 1 && this.ROOT_PATH in onCompletionDef! && typeof onCompletionDef.onCompletion === 'object'
        );
    }

    getRootPath(): string {
        return OnCompletionVisualEntity.ROOT_PATH;
    }

    getId(): string {
        return this.id;
    }

    setId(id: string): void {
        this.id = id;
        this.onCompletionDef.onCompletion.id = id;
    }

    getNodeInteraction(data: IVisualizationNodeData): NodeInteraction {
        const stepsProperties = ComponentSchemaService.getProcessorStepsProperties(
            (data as WorkflowVisualEntityData).processorName as keyof ProcessorDefinition,
        );
        const canHavePreviousStep = ComponentSchemaService.canHavePreviousStep(
            (data as WorkflowVisualEntityData).processorName,
        );
        const canHaveChildren = stepsProperties.find((property) => property.type === 'branch') !== undefined;
        const canHaveSpecialChildren = Object.keys(stepsProperties).length > 1;
        const canReplaceStep = data.path !== OnCompletionVisualEntity.ROOT_PATH;
        const canRemoveStep = data.path !== OnCompletionVisualEntity.ROOT_PATH;
        const canBeDisabled = ComponentSchemaService.canBeDisabled((data as WorkflowVisualEntityData).processorName);

        return {
            canHavePreviousStep,
            canHaveNextStep: canHavePreviousStep,
            canHaveChildren,
            canHaveSpecialChildren,
            canReplaceStep,
            canRemoveStep,
            canRemoveFlow: data.path === OnCompletionVisualEntity.ROOT_PATH,
            canBeDisabled,
        };
    }

    getNodeValidationText(path?: string | undefined): string | undefined {
        const componentVisualSchema = this.getComponentSchema(path);
        if (!componentVisualSchema) return undefined;

        return ModelValidationService.validateNodeStatus(componentVisualSchema);
    }

    toVizNode(): IVisualizationNode {
        const onCompletionGroupNode = NodeMapperService.getVizNode(
            OnCompletionVisualEntity.ROOT_PATH,
            { processorName: OnCompletionVisualEntity.ROOT_PATH as keyof ProcessorDefinition },
            this.onCompletionDef,
        );
        onCompletionGroupNode.data.entity = this;
        onCompletionGroupNode.data.isGroup = true;
        onCompletionGroupNode.data.icon = NodeIconResolver.getIcon(this.type, NodeIconType.Entity);

        return onCompletionGroupNode;
    }

    toJSON(): { onCompletion: OnCompletion } {
        return { onCompletion: this.onCompletionDef.onCompletion };
    }

    protected getRootUri(): string | undefined {
        return undefined;
    }
}
