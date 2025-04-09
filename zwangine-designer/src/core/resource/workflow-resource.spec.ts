import {it , expect, describe, vi} from "vitest"
import { WorkflowResource } from "./workflow-resource";
import {workflowAbstractRepresentation, workflowYaml} from "@/stubs/workflow.ts";
import { SourceSchemaType } from "@/core/model/schema/schema";
import { YamlDsl } from "@/@types";
import { WorkflowVisualEntity } from "@/core/entity/workflow-visual-entity";
import {fromJson} from "@/stubs/from.ts";
import { NonVisualEntity } from "@/core/entity/non-visual-entity";
import { EntityType } from "../model/entity/base-entity";
import { ComponentFilterService } from "@/core/component-filter-service";
import {ResourceFactory} from "@/core/resource/resource-factory.ts";
import {AddStepMode} from "@/core/model/entity/base-visual-entity.ts";

it('should create WorkflowResource', () => {
    const resource = new WorkflowResource([workflowAbstractRepresentation]);
    expect(resource.getType()).toEqual(SourceSchemaType.Workflow);
    expect(resource.getVisualEntities().length).toEqual(1);
    expect(resource.getEntities().length).toEqual(0);
});

it('should initialize  Workflow if no args is specified', () => {
    const resource = new WorkflowResource(undefined);
    expect(resource.getType()).toEqual(SourceSchemaType.Workflow);
    expect(resource.getEntities()).toEqual([]);
    expect(resource.getVisualEntities()).toEqual([]);
});

describe('function Object() { [native code] }', () => {
    const testCases: [YamlDsl, unknown][] = [
        // Good cases
        [[workflowAbstractRepresentation], WorkflowVisualEntity],
        [[workflowAbstractRepresentation], WorkflowVisualEntity],
        [[{ from: { uri: 'direct:foo', steps: [] } }], WorkflowVisualEntity],
        [[{ from: { uri: 'direct:foo' } }] as YamlDsl, WorkflowVisualEntity],
        [[], undefined],

        // Temporary good cases
        [workflowAbstractRepresentation as unknown as YamlDsl, WorkflowVisualEntity],
        [fromJson as unknown as YamlDsl, WorkflowVisualEntity],
        [{ from: { uri: 'direct:foo', steps: [] } } as unknown as YamlDsl, WorkflowVisualEntity],
        [{ from: { uri: 'direct:foo' } } as unknown as YamlDsl, WorkflowVisualEntity],

        // Bad cases
        [{ from: 'direct:foo' } as unknown as YamlDsl, NonVisualEntity],
        [{} as YamlDsl, NonVisualEntity],
        [undefined as unknown as YamlDsl, undefined],
        [null as unknown as YamlDsl, undefined],
    ];
    it.each(testCases)('should return the appropriate entity for: %s', (json, expected) => {
        const resource = new WorkflowResource(json);
        const firstEntity = resource.getVisualEntities()[0] ?? resource.getEntities()[0];

        if (typeof expected === 'function') {
            expect(firstEntity).toBeInstanceOf(expected);
        } else {
            expect(firstEntity).toEqual(expected);
        }
    });
});

describe('addNewEntity', () => {
    it('should add new entity and return its ID', () => {
        const resource = new WorkflowResource();
        const id = resource.addNewEntity();

        expect(resource.getVisualEntities()).toHaveLength(1);
        expect(resource.getVisualEntities()[0].id).toEqual(id);
    });

    it('should add new entities at the end of the list and return its ID', () => {
        const resource = new WorkflowResource();
        resource.addNewEntity();
        const id = resource.addNewEntity(EntityType.Workflow);

        expect(resource.getVisualEntities()).toHaveLength(2);
        expect(resource.getVisualEntities()[1].id).toEqual(id);
    });

    it('should add OnException entity at the beginning of the list and return its ID', () => {
        const resource = new WorkflowResource();
        resource.addNewEntity();
        const id = resource.addNewEntity(EntityType.OnException);

        expect(resource.getVisualEntities()).toHaveLength(2);
        expect(resource.getVisualEntities()[0].id).toEqual(id);
    });


    it('should add OnCompletion entity at the beginning of the list and return its ID', () => {
        const resource = new WorkflowResource();
        resource.addNewEntity();
        const id = resource.addNewEntity(EntityType.OnCompletion);

        expect(resource.getVisualEntities()).toHaveLength(2);
        expect(resource.getVisualEntities()[0].id).toEqual(id);
    });
});

it('should return the right type', () => {
    const resource = new WorkflowResource();
    expect(resource.getType()).toEqual(SourceSchemaType.Workflow);
});

it('should allow consumers to have multiple visual entities', () => {
    const resource = new WorkflowResource();
    expect(resource.supportsMultipleVisualEntities()).toEqual(true);
});

it('should return visual entities', () => {
    const resource = new WorkflowResource([workflowAbstractRepresentation]);
    expect(resource.getVisualEntities()).toHaveLength(1);
    expect(resource.getVisualEntities()[0]).toBeInstanceOf(WorkflowVisualEntity);
    expect(resource.getEntities()).toHaveLength(0);
});


describe('toJSON', () => {
    it('should return JSON', () => {
        const resource = new WorkflowResource([workflowAbstractRepresentation]);
        expect(resource.toJSON()).toMatchSnapshot();
    });

    it.todo('should position the ID at the top of the JSON');
    it.todo('should position the parameters after the ID');
});


describe('removeEntity', () => {
    it('should not do anything if the ID is not provided', () => {
        const resource = new WorkflowResource([workflowAbstractRepresentation]);

        resource.removeEntity();

        expect(resource.getVisualEntities()).toHaveLength(1);
    });

    it('should not do anything when providing a non existing ID', () => {
        const resource = new WorkflowResource([workflowAbstractRepresentation]);

        resource.removeEntity('non-existing-id');

        expect(resource.getVisualEntities()).toHaveLength(1);
    });

    it('should allow to remove an entity', () => {
        const resource = new WorkflowResource([workflowAbstractRepresentation, fromJson]);
        const camelWorkflowEntity = resource.getVisualEntities()[0];

        resource.removeEntity(camelWorkflowEntity.id);

        expect(resource.getVisualEntities()).toHaveLength(1);
    });

    it('should NOT create a new entity after deleting them all', () => {
        const resource = new WorkflowResource([workflowAbstractRepresentation]);
        const camelWorkflowEntity = resource.getVisualEntities()[0];

        resource.removeEntity(camelWorkflowEntity.id);

        expect(resource.getVisualEntities()).toHaveLength(0);
    });
});

describe('getCompatibleComponents', () => {
    it('should delegate to the ComponentFilterService', () => {
        const filterSpy = vi.spyOn(ComponentFilterService, 'getCompatibleComponents');

        const resource = ResourceFactory.createResource(workflowYaml);
        resource.getCompatibleComponents(AddStepMode.ReplaceStep, { path: 'from', label: 'timer' });

        expect(filterSpy).toHaveBeenCalledWith(AddStepMode.ReplaceStep, { path: 'from', label: 'timer' }, undefined);
    });
});

describe('toJson', () => {
    const testCases: [YamlDsl][] = [
        [[workflowAbstractRepresentation]],
        [[fromJson]],
        [[{ from: { uri: 'direct:foo', steps: [] } }]],
        [{ from: 'direct:foo' } as unknown as YamlDsl],
        [{ from: { uri: 'direct:foo' } } as unknown as YamlDsl],
        [[{ beans: [] }]],
        [[{ errorHandler: {} }]],
        [[{ intercept: {} }]],
        [[{ interceptFrom: {} }]],
        [{ interceptSendToEndpoint: {} } as unknown as YamlDsl],
        [{ onCompletion: {} } as unknown as YamlDsl],
        [{ onException: {} } as unknown as YamlDsl],
        [{ rest: {} } as unknown as YamlDsl],
        [[{ restConfiguration: {} }]],
        [{ workflow: {} } as unknown as YamlDsl],
        [[{ workflowConfiguration: {} }]],
        [[{ workflowTemplate: {} }] as unknown as YamlDsl],
        [{ templatedWorkflow: {} } as unknown as YamlDsl],
        [{ anotherUnknownContent: {} } as unknown as YamlDsl],
        [{} as YamlDsl],
    ];
    it.each(testCases)('should not throw error when calling: %s', (json) => {
        const resource = new WorkflowResource(json);
        const firstEntity = resource.getVisualEntities()[0] ?? resource.getEntities()[0];
        expect(firstEntity.toJSON()).not.toBeUndefined();
    });
});