import {FromDefinition, WorkflowDefinition } from "@/@types";
import {isDefined, setValue} from "@/lib/utility";
import {getRandomId} from "@/lib/ua-utils";
import {EntityType} from "../model/entity/base-entity.ts";
import {DefinedComponent} from "../model/catalog/catalog-index.ts";
import {AddStepMode, IVisualizationNodeData} from "../model/entity/base-visual-entity.ts";
import {AbstractVisualEntity} from "./abstract-visual-entity.ts";
import {ComponentDefaultService} from "../component-default-service.ts";


/** Very basic check to determine whether this object is a Workflow */
export const isWorkflow = (rawEntity: unknown): rawEntity is { workflow: WorkflowDefinition } => {
    if (!isDefined(rawEntity) || Array.isArray(rawEntity) || typeof rawEntity !== 'object') {
        return false;
    }

    const objectKeys = Object.keys(rawEntity!);

    return (
        objectKeys.length === 1 &&
        'workflow' in rawEntity! &&
        typeof rawEntity.workflow === 'object' &&
        'from' in rawEntity.workflow!
    );
};

/** Very basic check to determine whether this object is a From */
export const isFrom = (rawEntity: unknown): rawEntity is { from: FromDefinition } => {
    if (!isDefined(rawEntity) || Array.isArray(rawEntity) || typeof rawEntity !== 'object') {
        return false;
    }

    const objectKeys = Object.keys(rawEntity!);
    const isFromHolder = objectKeys.length === 1 && objectKeys[0] === 'from';
    const isValidUriField = typeof (rawEntity as { from: FromDefinition })?.from?.uri === 'string';

    return isFromHolder && isValidUriField;
};

const getDefaultWorkflowDefinition = (fromDefinition?: { from: FromDefinition }): { workflow: WorkflowDefinition } => ({
    workflow: {
        from: fromDefinition?.from ?? {
            uri: '',
            steps: [],
        },
    },
});

export class WorkflowVisualEntity extends AbstractVisualEntity<{ workflow: WorkflowDefinition }> {
    id: string;
    readonly type = EntityType.Workflow;
    static readonly ROOT_PATH = 'workflow';

    constructor(workflowRaw: { workflow: WorkflowDefinition } | { from: FromDefinition } | undefined) {
        let workflowDef: { workflow: WorkflowDefinition };
        let workflowRawId: string | undefined;
        if (isFrom(workflowRaw)) {
            workflowDef = getDefaultWorkflowDefinition(workflowRaw);
            workflowRawId = workflowRaw.from.id;
        } else if (isWorkflow(workflowRaw)) {
            workflowDef = workflowRaw;
            workflowRawId = workflowRaw.workflow?.id;
        } else {
            workflowDef = getDefaultWorkflowDefinition();
        }

        super(workflowDef);
        const id = workflowRawId ?? getRandomId('workflow');
        this.id = id;
        this.entityDef.workflow.id = this.id;
        //this.entityDef.workflow.from.uri = this.entityDef.workflow.from.uri+":"+String(this.entityDef.workflow.from.parameters[""]);
    }

    static isApplicable(workflowDef: unknown): workflowDef is { workflow: WorkflowDefinition } | { from: FromDefinition } {
        return isWorkflow(workflowDef) || isFrom(workflowDef);
    }

    getRootPath(): string {
        return WorkflowVisualEntity.ROOT_PATH;
    }

    /** Internal API methods */
    setId(workflowId: string): void {
        this.id = workflowId;
        this.entityDef.workflow.id = this.id;
    }

    toJSON(): { workflow: WorkflowDefinition } {
        return { workflow: this.entityDef.workflow };
    }

    addStep(options: {
        definedComponent: DefinedComponent;
        mode: AddStepMode;
        data: IVisualizationNodeData;
        targetProperty?: string | undefined;
    }): void {
        /** Replace the root `from` step */
        if (
            options.mode === AddStepMode.ReplaceStep &&
            options.data.path === `${this.getRootPath()}.from` &&
            isDefined(this.entityDef.workflow.from)
        ) {
            const fromValue = ComponentDefaultService.getDefaultFromDefinitionValue(options.definedComponent);
            Object.assign(this.entityDef.workflow.from, fromValue);
            return;
        }
        super.addStep(options);
    }

    removeStep(path?: string): void {
        if (!path) return;
        /**
         * If there's only one path segment, it means the target is the `from` property of the workflow
         * therefore we replace it with an empty object
         */
        if (path === `${this.getRootPath()}.from`) {
            setValue(this.entityDef, `${this.getRootPath()}.from.uri`, '');
            return;
        }

        super.removeStep(path);
    }

    updateModel(path: string | undefined, value: unknown): void {
        super.updateModel(path, value);
        if (isDefined(this.entityDef.workflow.id)) this.id = this.entityDef.workflow.id;
    }

    protected getRootUri(): string | undefined {
        return this.entityDef.workflow.from?.uri;
    }
}
