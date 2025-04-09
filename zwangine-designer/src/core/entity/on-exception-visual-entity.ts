import { NodeIconResolver, NodeIconType, isDefined } from '@/lib/utility';
import {OnException, ProcessorDefinition} from '@/@types';
import { getRandomId } from '@/lib/ua-utils';
import {
    BaseVisualEntity,
    IVisualizationNode,
    IVisualizationNodeData,
    NodeInteraction
} from '../model/entity/base-visual-entity.ts';
import { EntityType } from '../model/entity/base-entity.ts';
import { WorkflowVisualEntityData } from '../model/component-type.ts';
import { AbstractVisualEntity } from './abstract-visual-entity.ts';
import {ComponentSchemaService} from "../schema/component-schema-service.ts";
import {ModelValidationService} from "../model-validation-service.ts";
import {NodeMapperService} from "../mapper/node-mapper-service.ts";

export class OnExceptionVisualEntity
    extends  AbstractVisualEntity<{ onException: OnException }>
    implements BaseVisualEntity
{
    id: string;
    readonly type = EntityType.OnException;
    private static readonly ROOT_PATH = 'onException';

    constructor(public onExceptionDef: { onException: OnException } = { onException: {} }) {
        super(onExceptionDef);
        const id = onExceptionDef.onException.id ?? getRandomId(OnExceptionVisualEntity.ROOT_PATH);
        this.id = id;
        this.onExceptionDef.onException.id = id;
    }

    static isApplicable(onExceptionDef: unknown): onExceptionDef is { onException: OnException } {
        if (!isDefined(onExceptionDef) || Array.isArray(onExceptionDef) || typeof onExceptionDef !== 'object') {
            return false;
        }

        const objectKeys = Object.keys(onExceptionDef!);

        return (
            objectKeys.length === 1 && this.ROOT_PATH in onExceptionDef! && typeof onExceptionDef.onException === 'object'
        );
    }

    getRootPath(): string {
        return OnExceptionVisualEntity.ROOT_PATH;
    }

    getId(): string {
        return this.id;
    }

    setId(id: string): void {
        this.id = id;
        this.onExceptionDef.onException.id = id;
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
        const canReplaceStep = data.path !== OnExceptionVisualEntity.ROOT_PATH;
        const canRemoveStep = data.path !== OnExceptionVisualEntity.ROOT_PATH;
        const canBeDisabled = ComponentSchemaService.canBeDisabled((data as WorkflowVisualEntityData).processorName);

        return {
            canHavePreviousStep,
            canHaveNextStep: canHavePreviousStep,
            canHaveChildren,
            canHaveSpecialChildren,
            canReplaceStep,
            canRemoveStep,
            canRemoveFlow: data.path === OnExceptionVisualEntity.ROOT_PATH,
            canBeDisabled,
        };
    }

    getNodeValidationText(path?: string | undefined): string | undefined {
        const componentVisualSchema = this.getComponentSchema(path);
        if (!componentVisualSchema) return undefined;

        return ModelValidationService.validateNodeStatus(componentVisualSchema);
    }

    toVizNode(): IVisualizationNode<IVisualizationNodeData> {
        const onExceptionGroupNode = NodeMapperService.getVizNode(
            OnExceptionVisualEntity.ROOT_PATH,
            { processorName: OnExceptionVisualEntity.ROOT_PATH as keyof ProcessorDefinition },
            this.onExceptionDef,
        );
        onExceptionGroupNode.data.entity = this;
        onExceptionGroupNode.data.isGroup = true;
        onExceptionGroupNode.data.icon = NodeIconResolver.getIcon(this.type, NodeIconType.Entity);

        return onExceptionGroupNode;
    }

    toJSON(): { onException: OnException } {
        return { onException: this.onExceptionDef.onException };
    }

    protected getRootUri(): string | undefined {
        return undefined;
    }
}
