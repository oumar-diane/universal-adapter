import Ajv, { ValidateFunction } from 'ajv';
import addFormats from 'ajv-formats';
import { NodeIconResolver, NodeIconType, getValue, isDefined, setValue } from '@/lib/utility';
import {ProcessorDefinition, WorkflowConfigurationDefinition} from '@/@types';
import { SchemaService } from '@/components/form/schema-service.ts';
import { getRandomId } from '@/lib/ua-utils';
import { AbstractVisualEntity } from './abstract-visual-entity.ts';
import {
    BaseVisualEntity, IVisualizationNode,
    IVisualizationNodeData,
    NodeInteraction,
    VisualComponentSchema
} from "../model/entity/base-visual-entity.ts";
import {EntityType} from "../model/entity/base-entity.ts";
import {CatalogKind} from "../model/catalog/catalog-kind.ts";
import {SchemaDefinition} from "../model/schema/schema.ts";
import {CatalogService} from "../catalog/catalog-service.ts";
import { createVisualizationNode } from '../visualization-node.ts';
import {ComponentSchemaService} from "../schema/component-schema-service.ts";
import {NodeMapperService} from "../mapper/node-mapper-service.ts";

export class WorkflowConfigurationVisualEntity
    extends AbstractVisualEntity<{ workflowConfiguration: WorkflowConfigurationDefinition }>
    implements BaseVisualEntity
{
    id: string;
    readonly type = EntityType.WorkflowConfiguration;
    static readonly ROOT_PATH = 'workflowConfiguration';
    private schemaValidator: ValidateFunction<WorkflowConfigurationDefinition> | undefined;
    private readonly OMIT_FORM_FIELDS = [
        ...SchemaService.OMIT_FORM_FIELDS,
        'intercept',
        'interceptFrom',
        'interceptSendToEndpoint',
        'onException',
        'onCompletion',
    ];

    constructor(
        public workflowConfigurationDef: { workflowConfiguration: WorkflowConfigurationDefinition } = { workflowConfiguration: {} },
    ) {
        super(workflowConfigurationDef);
        const id = workflowConfigurationDef.workflowConfiguration.id ?? getRandomId(this.getRootPath());
        this.id = id;
        this.workflowConfigurationDef.workflowConfiguration.id = id;
    }

    static isApplicable(
        workflowConfigurationDef: unknown,
    ): workflowConfigurationDef is { workflowConfiguration: WorkflowConfigurationDefinition } {
        if (
            !isDefined(workflowConfigurationDef) ||
            Array.isArray(workflowConfigurationDef) ||
            typeof workflowConfigurationDef !== 'object'
        ) {
            return false;
        }

        const objectKeys = Object.keys(workflowConfigurationDef!);

        return (
            objectKeys.length === 1 &&
            this.ROOT_PATH in workflowConfigurationDef! &&
            typeof workflowConfigurationDef.workflowConfiguration === 'object'
        );
    }

    getRootPath(): string {
        return WorkflowConfigurationVisualEntity.ROOT_PATH;
    }

    getId(): string {
        return this.id;
    }

    setId(id: string): void {
        this.id = id;
    }

    getTooltipContent(path?: string): string {
        if (path === this.getRootPath()) {
            return 'workflowConfiguration';
        }

        return super.getTooltipContent(path);
    }

    getComponentSchema(path?: string | undefined): VisualComponentSchema | undefined {
        if (path === this.getRootPath()) {
            const schema = CatalogService.getComponent(CatalogKind.Entity, 'workflowConfiguration');
            return {
                schema: schema?.propertiesSchema || {},
                definition: Object.assign({}, this.workflowConfigurationDef.workflowConfiguration),
            };
        }

        return super.getComponentSchema(path);
    }

    getOmitFormFields(): string[] {
        return this.OMIT_FORM_FIELDS;
    }

    updateModel(path: string | undefined, value: unknown): void {
        if (!path) return;

        setValue(this.workflowConfigurationDef, path, value);

        if (!isDefined(this.workflowConfigurationDef.workflowConfiguration)) {
            this.workflowConfigurationDef.workflowConfiguration = {};
        }
    }

    getNodeInteraction(data: IVisualizationNodeData): NodeInteraction {
        if (data.path === this.getRootPath()) {
            return {
                canHavePreviousStep: false,
                canHaveNextStep: false,
                canHaveChildren: false,
                canHaveSpecialChildren: true,
                canRemoveStep: false,
                canReplaceStep: false,
                canRemoveFlow: true,
                canBeDisabled: false,
            };
        }

        return super.getNodeInteraction(data);
    }

    getNodeValidationText(): string | undefined {
        const componentVisualSchema = this.getComponentSchema();
        if (!componentVisualSchema) return undefined;

        if (!this.schemaValidator) {
            this.schemaValidator = this.getValidatorFunction(componentVisualSchema.schema);
        }

        this.schemaValidator?.({ ...this.workflowConfigurationDef.workflowConfiguration });

        return this.schemaValidator?.errors?.map((error) => `'${error.instancePath}' ${error.message}`).join(',\n');
    }

    toVizNode(): IVisualizationNode {
        const workflowConfigurationGroupNode = createVisualizationNode(this.id, {
            path: this.getRootPath(),
            entity: this,
            isGroup: true,
            icon: NodeIconResolver.getIcon(this.type, NodeIconType.Entity),
            processorName: this.getRootPath(),
        });

        ComponentSchemaService.getProcessorStepsProperties(this.getRootPath() as keyof ProcessorDefinition).forEach(
            (stepsProperty) => {
                const childEntities = getValue(this.workflowConfigurationDef.workflowConfiguration, stepsProperty.name, []);
                if (!Array.isArray(childEntities)) return;

                childEntities.forEach((childEntity, index) => {
                    const childNode = NodeMapperService.getVizNode(
                        `${this.getRootPath()}.${stepsProperty.name}.${index}.${Object.keys(childEntity)[0]}`,
                        {
                            processorName: stepsProperty.name as keyof ProcessorDefinition,
                        },
                        this.workflowConfigurationDef,
                    );

                    workflowConfigurationGroupNode.addChild(childNode);
                });
            },
        );

        return workflowConfigurationGroupNode;
    }

    toJSON(): { workflowConfiguration: WorkflowConfigurationDefinition } {
        return { workflowConfiguration: this.workflowConfigurationDef.workflowConfiguration };
    }

    protected getRootUri(): string | undefined {
        return undefined;
    }

    private getValidatorFunction(
        schema: SchemaDefinition['schema'],
    ): ValidateFunction<WorkflowConfigurationDefinition> | undefined {
        const ajv = new Ajv({
            strict: false,
            allErrors: true,
            useDefaults: 'empty',
        });
        addFormats(ajv);

        let schemaValidator: ValidateFunction<WorkflowConfigurationDefinition> | undefined;
        try {
            schemaValidator = ajv.compile<WorkflowConfigurationDefinition>(schema);
        } catch (error) {
            console.error('Could not compile schema', error);
        }

        return schemaValidator;
    }
}
