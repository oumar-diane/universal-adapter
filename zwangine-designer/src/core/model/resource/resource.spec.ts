import { ResourceFactory } from "@/core/resource/resource-factory";
import {it, expect} from "vitest"
import { SourceSchemaType } from "@/core/model/schema/schema";

it('should create an empty WorkflowResource if no args is specified', () => {
    const resource = ResourceFactory.createResource();
    expect(resource.getType()).toEqual(SourceSchemaType.Workflow);
    expect(resource.getEntities()).toEqual([]);
    expect(resource.getVisualEntities()).toEqual([]);
});

it('should create an empty WorkflowResource if a camel.yaml path is specified', () => {
    const resource = ResourceFactory.createResource(undefined, { path: 'my-workflow.yaml' });
    expect(resource.getType()).toEqual(SourceSchemaType.Workflow);
    expect(resource.getEntities()).toEqual([]);
    expect(resource.getVisualEntities()).toEqual([]);
});


