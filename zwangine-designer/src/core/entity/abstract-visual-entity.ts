import {SchemaService} from "@/components/form/schema-service.ts";

import {
    camelCaseToSpaces,
    getArrayProperty,
    getValue,
    isDefined,
    NodeIconResolver,
    NodeIconType,
    setValue
} from "@/lib/utility";
import {ProcessorDefinition} from "@/@types";
import {createVisualizationNode} from "../visualization-node.ts";
import {
    AddStepMode,
    BaseVisualEntity, IVisualizationNode,
    IVisualizationNodeData,
    NodeInteraction,
    VisualComponentSchema
} from "../model/entity/base-visual-entity.ts";
import { EntityType } from "../model/entity/base-entity.ts";
import { NodeLabelType } from "../model/schema/schema.ts";
import { DefinedComponent } from "../model/catalog/catalog-index.ts";
import {ProcessorStepsProperties, WorkflowVisualEntityData} from "../model/component-type.ts";
import { ComponentSchemaService } from "../schema/component-schema-service.ts";
import {ModelValidationService} from "../model-validation-service.ts";
import {NodeMapperService} from "../mapper/node-mapper-service.ts";
import {ComponentDefaultService} from "../component-default-service.ts";

export abstract class AbstractVisualEntity<T extends object> implements BaseVisualEntity {
    protected constructor(public entityDef: T) {}

    abstract id: string;
    abstract type: EntityType;
    abstract getRootPath(): string;
    abstract setId(id: string): void;
    abstract toJSON(): unknown;
    protected abstract getRootUri(): string | undefined;

    getId(): string {
        return this.id;
    }

    getNodeLabel(path?: string, nodeLabeltype?:NodeLabelType): string {
        if (!path) return '';
        // handle the root path
        if (path === this.getRootPath()) {
            const description: string | undefined = getValue(this.entityDef, `${this.getRootPath()}.description`);
            if (nodeLabeltype === NodeLabelType.Description && description) {
                return description;
            }

            return this.id;
        }
        // handle the rest of the paths
        const dslAbstractRepresentation = getValue(this.entityDef, path);

        return ComponentSchemaService.getNodeLabel(
            ComponentSchemaService.getComponentLookup(path, dslAbstractRepresentation),
            dslAbstractRepresentation,
            nodeLabeltype
        );
    }

    getNodeTitle(path?: string): string {
        if (!path) return '';
        if (path === this.getRootPath()) {
            return camelCaseToSpaces(this.getRootPath(), { capitalize: true });
        }

        const dslAbstractRepresentation = getValue(this.entityDef, path);

        return ComponentSchemaService.getNodeTitle(
            ComponentSchemaService.getComponentLookup(path, dslAbstractRepresentation),
        );
    }

    getTooltipContent(path?: string): string {
        if (!path) return '';
        const componentModel = getValue(this.entityDef, path);

        const content = ComponentSchemaService.getTooltipContent(
            ComponentSchemaService.getComponentLookup(path, componentModel),
        );

        return content;
    }

    getComponentSchema(path?: string): VisualComponentSchema | undefined {
        if (!path) return undefined;

        const dslAbstractRepresentation = getValue(this.entityDef, path);
        const visualComponentSchema = ComponentSchemaService.getVisualComponentSchema(path, dslAbstractRepresentation);

        /** Overriding parameters with an empty object When the parameters property is mistakenly set to null */
        if (visualComponentSchema?.definition?.parameters === null) {
            visualComponentSchema.definition.parameters = {};
        }

        return visualComponentSchema;
    }

    getOmitFormFields(): string[] {
        return SchemaService.OMIT_FORM_FIELDS;
    }

    updateModel(path: string | undefined, value: unknown): void {
        if (!path) return;
        const updatedValue = ComponentSchemaService.getMultiValueSerializedDefinition(path, value);

        setValue(this.entityDef, path, updatedValue);
    }

    /**
     * Add a step to the workflow
     *
     * path examples:
     *      workflow.from
     *      workflow.from.steps.0.setHeader
     *      workflow.from.steps.1.choice.when.0
     *      workflow.from.steps.1.choice.when.0.steps.0.setHeader
     *      workflow.from.steps.1.choice.otherwise
     *      workflow.from.steps.1.choice.otherwise.steps.0.setHeader
     *      workflow.from.steps.2.doTry.doCatch.0
     *      workflow.from.steps.2.doTry.doCatch.0.steps.0.setHeader
     */
    addStep(options: {
        definedComponent: DefinedComponent;
        mode: AddStepMode;
        data: IVisualizationNodeData;
        targetProperty?: string;
    }) {
        if (options.data.path === undefined) return;
        const defaultValue = ComponentDefaultService.getDefaultNodeDefinitionValue(options.definedComponent);
        const stepsProperties = ComponentSchemaService.getProcessorStepsProperties(
            (options.data as WorkflowVisualEntityData).processorName as keyof ProcessorDefinition,
        );

        if (options.mode === AddStepMode.InsertChildStep || options.mode === AddStepMode.InsertSpecialChildStep) {
            this.insertChildStep(options, stepsProperties, defaultValue);
            return;
        }

        const pathArray = options.data.path.split('.');
        const last = pathArray[pathArray.length - 1];
        const penultimate = pathArray[pathArray.length - 2];

        /**
         * If the last segment is a string and the penultimate is a number, it means the target is member of an array
         * therefore we need to look for the array and insert the element at the given index + 1
         *
         * f.i. workflow.from.steps.0.setHeader
         * penultimate: 0
         * last: setHeader
         */
        if (!Number.isInteger(Number(last)) && Number.isInteger(Number(penultimate))) {
            /** If we're in Append mode, we need to insert the step after the selected index hence `Number(penultimate) + 1` */
            const desiredStartIndex = options.mode === AddStepMode.AppendStep ? Number(penultimate) + 1 : Number(penultimate);

            /** If we're in Replace mode, we need to delete the existing step */
            const deleteCount = options.mode === AddStepMode.ReplaceStep ? 1 : 0;

            const stepsArray: ProcessorDefinition[] = getArrayProperty(this.entityDef, pathArray.slice(0, -2).join('.'));
            stepsArray.splice(desiredStartIndex, deleteCount, defaultValue);

            return;
        }
    }

    canDragNode(path?: string) {
        if (!isDefined(path)) return false;

        return path !== 'workflow.from' && path !== 'template.from';
    }

    canDropOnNode(path?: string) {
        return this.canDragNode(path);
    }

    /** To Do: combine with addstep()
     *  Try to re-use insertChildStep()
     */
    moveNodeTo(options: { draggedNodePath: string; droppedNodePath?: string }) {
        if (options.droppedNodePath === undefined) return;

        const pathArray = options.droppedNodePath.split('.');
        const last = pathArray[pathArray.length - 1];
        const penultimate = pathArray[pathArray.length - 2];

        const componentPath = options.draggedNodePath.split('.');
        let stepsArray: ProcessorDefinition[];

        if (!Number.isInteger(Number(last)) && Number.isInteger(Number(penultimate))) {
            const componentModel = getValue(this.entityDef, componentPath?.slice(0, -1));
            stepsArray = getArrayProperty(this.entityDef, pathArray.slice(0, -2).join('.'));

            /** Remove the dragged node */
            this.removeStep(options.draggedNodePath);

            /** Add the dragged node before the drop target */
            const desiredStartIndex = last === 'placeholder' ? 0 : Number(penultimate);
            stepsArray.splice(desiredStartIndex, 0, componentModel);
        }

        if (Number.isInteger(Number(last)) && !Number.isInteger(Number(penultimate))) {
            const componentModel = getValue(this.entityDef, componentPath);
            stepsArray = getArrayProperty(this.entityDef, pathArray.slice(0, -1).join('.'));

            /** Remove the dragged node */
            this.removeStep(options.draggedNodePath);

            /** Add the dragged node before the drop target */
            stepsArray.splice(Number(last), 0, componentModel);
        }
    }

    removeStep(path?: string): void {
        if (!path) return;
        const pathArray = path.split('.');
        const last = pathArray[pathArray.length - 1];
        const penultimate = pathArray[pathArray.length - 2];

        /**
         * If the last segment is a number, it means the target object is a member of an array
         * therefore we need to look for the array and remove the element at the given index
         *
         * f.i. workflow.from.steps.1.choice.when.0
         * last: 0
         */
        let array = getValue(this.entityDef, pathArray.slice(0, -1), []);
        if (Number.isInteger(Number(last)) && Array.isArray(array)) {
            array.splice(Number(last), 1);

            return;
        }

        /**
         * If the last segment is a word and the penultimate is a number, it means the target is an object
         * potentially a Processor, that belongs to an array, therefore we remove it entirely
         *
         * f.i. workflow.from.steps.1.choice
         * last: choice
         * penultimate: 1`
         */
        array = getValue(this.entityDef, pathArray.slice(0, -2), []);
        if (!Number.isInteger(Number(last)) && Number.isInteger(Number(penultimate)) && Array.isArray(array)) {
            array.splice(Number(penultimate), 1);

            return;
        }

        /**
         * If both the last and penultimate segment are words, it means the target is a property of an object
         * therefore we delete it
         *
         * f.i. workflow.from.steps.1.choice.otherwise
         * last: otherwise
         * penultimate: choice
         */
        const object = getValue(this.entityDef, pathArray.slice(0, -1), {});
        if (!Number.isInteger(Number(last)) && !Number.isInteger(Number(penultimate)) && typeof object === 'object') {
            delete object[last];
        }
    }

    getNodeInteraction(data: IVisualizationNodeData): NodeInteraction {
        const processorName = (data as WorkflowVisualEntityData).processorName;
        const canHavePreviousStep = ComponentSchemaService.canHavePreviousStep(processorName);
        const stepsProperties = ComponentSchemaService.getProcessorStepsProperties(processorName);
        const canHaveChildren = stepsProperties.find((property) => property.type === 'branch') !== undefined;
        const canHaveSpecialChildren = Object.keys(stepsProperties).length > 1;
        const canReplaceStep = ComponentSchemaService.canReplaceStep(processorName);
        const canRemoveStep = !ComponentSchemaService.DISABLED_REMOVE_STEPS.includes(processorName);
        const canRemoveFlow = data.path === this.getRootPath();
        const canBeDisabled = ComponentSchemaService.canBeDisabled(processorName);

        return {
            canHavePreviousStep,
            canHaveNextStep: canHavePreviousStep,
            canHaveChildren,
            canHaveSpecialChildren,
            canReplaceStep,
            canRemoveStep,
            canRemoveFlow,
            canBeDisabled,
        };
    }

    getNodeValidationText(path?: string | undefined): string | undefined {
        const componentVisualSchema = this.getComponentSchema(path);
        if (!componentVisualSchema) return undefined;

        return ModelValidationService.validateNodeStatus(componentVisualSchema);
    }

    toVizNode(): IVisualizationNode {
        const workflowGroupNode = createVisualizationNode(this.getRootPath(), {
            path: this.getRootPath(),
            entity: this,
            isGroup: true,
            icon: NodeIconResolver.getIcon(this.type, NodeIconType.Entity),
            processorName: 'workflow',
        });


        const fromNode = NodeMapperService.getVizNode(
            `${this.getRootPath()}.from`,
            {
                processorName: 'from' as keyof ProcessorDefinition,
                componentName: ComponentSchemaService.getComponentNameFromUri(this.getRootUri()!),
            },
            this.entityDef,
        );

        if (!this.getRootUri()) {
            fromNode.data.icon = NodeIconResolver.getPlaceholderIcon();
        }
        workflowGroupNode.addChild(fromNode);

        fromNode.getChildren()?.forEach((child: IVisualizationNode<IVisualizationNodeData>, index: number) => {
            workflowGroupNode.addChild(child);
            if (index === 0) {
                fromNode.setNextNode(child);
                child.setPreviousNode(fromNode);
            }

            const previousChild = fromNode.getChildren()?.[index - 1];
            if (previousChild) {
                previousChild.setNextNode(child);
                child.setPreviousNode(previousChild);
            }
        });
        fromNode.getChildren()?.splice(0);
        fromNode.data.isGroup = false;

        return workflowGroupNode;
    }

    private insertChildStep(
        options: Parameters<AbstractVisualEntity<object>['addStep']>[0],
        stepsProperties: ProcessorStepsProperties[],
        defaultValue: ProcessorDefinition = {},
    ) {
        const property = stepsProperties.find((property) =>
            options.mode === AddStepMode.InsertChildStep ? 'steps' : options.definedComponent.name === property.name,
        );
        if (property === undefined) return;

        if (property.type === 'single-clause') {
            setValue(this.entityDef, `${options.data.path}.${property.name}`, defaultValue);
        } else {
            const arrayPath: ProcessorDefinition[] = getArrayProperty(
                this.entityDef,
                `${options.data.path}.${property.name}`,
            );
            arrayPath.unshift(defaultValue);
        }
    }
}
