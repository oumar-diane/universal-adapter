import {it, expect} from "vitest"
import { ResourceFactory } from "./resource-factory";
import { WorkflowResource } from "./workflow-resource";
import { SerializerType } from "../model/resource/resource-serializer";

it('should use YAML serializer for YAML content', () => {
    const yamlContent = '- from:\n    uri: timer:foo';
    const resource = ResourceFactory.createResource(yamlContent);
    expect(resource).toBeInstanceOf(WorkflowResource);
    expect(resource.getSerializerType()).toEqual(SerializerType.YAML);
});


it('should use YAML serializer when path ends with .yaml', () => {
    const resource = ResourceFactory.createResource(undefined, { path: 'test.yaml' });
    expect(resource).toBeInstanceOf(WorkflowResource);
    expect(resource.getSerializerType()).toEqual(SerializerType.YAML);
});


it('should use YAML serializer with path with YAML and XML content', () => {
    const xmlContent = '<?xml version="1.0" encoding="UTF-8"?><routes></routes>';
    const resource = ResourceFactory.createResource(xmlContent, { path: 'test.yaml' });
    expect(resource).toBeInstanceOf(WorkflowResource);
    expect(resource.getSerializerType()).toEqual(SerializerType.YAML);
});

it('should handle undefined source and path', () => {
    const resource = ResourceFactory.createResource();
    expect(resource).toBeInstanceOf(WorkflowResource);
    expect(resource.getSerializerType()).toEqual(SerializerType.YAML);
});

it('should handle empty options object', () => {
    const resource = ResourceFactory.createResource(undefined, {});
    expect(resource).toBeInstanceOf(WorkflowResource);
    expect(resource.getSerializerType()).toEqual(SerializerType.YAML);
})
