import {afterAll, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import catalogDefinition from '@/assets/resources/catalog-index.json';
import {getCatalogMap} from "@/stubs/test-load-catalog.ts";
import {CatalogService} from "../catalog/catalog-service.ts";
import {cloneDeep} from "lodash";
import {workflowAbstractRepresentation} from "@/stubs/workflow.ts";
import { CatalogKind } from "../model/catalog/catalog-kind.ts";
import {CatalogIndex} from "../model/catalog/catalog-index.ts";
import {NodeLabelType} from "../model/schema/schema.ts";
import {AddStepMode} from "../model/entity/base-visual-entity.ts";
import { WorkflowVisualEntity } from "./workflow-visual-entity.ts";
import {ComponentSchemaService} from "../schema/component-schema-service.ts";



let abstractVisualEntity: WorkflowVisualEntity;

beforeAll(async() => {
    const catalogMap = await getCatalogMap(catalogDefinition as CatalogIndex)
    CatalogService.setCatalogKey(CatalogKind.Component, catalogMap.componentCatalogMap);
    CatalogService.setCatalogKey(CatalogKind.Pattern, catalogMap.patternCatalogMap);
    CatalogService.setCatalogKey(CatalogKind.Processor, catalogMap.modelCatalogMap);
    CatalogService.setCatalogKey(CatalogKind.Entity, catalogMap.entitiesCatalog);
}, 100000);

beforeEach(() => {
    abstractVisualEntity = new WorkflowVisualEntity(cloneDeep(workflowAbstractRepresentation));
});

afterAll(() => {
    CatalogService.clearCatalogs();
});


describe('getNodeLabel', () => {
    it('should return an empty string if the path is `undefined`', () => {
        const result = abstractVisualEntity.getNodeLabel(undefined);
        expect(true).toBe(true)
        expect(result).toEqual('');
    });

    it('should return an empty string if the path is empty', () => {
        const result = abstractVisualEntity.getNodeLabel('');
        expect(result).toEqual('');
    });

    it('should return the ID as a node label by default', () => {
        const result = abstractVisualEntity.getNodeLabel('workflow');
        expect(result).toEqual('workflow-8888');
    });

    it('should return the description as a node label', () => {
        const dslAbstractRepresentation = cloneDeep(workflowAbstractRepresentation);
        dslAbstractRepresentation.workflow.description = 'description';
        abstractVisualEntity = new WorkflowVisualEntity(dslAbstractRepresentation);

        const result = abstractVisualEntity.getNodeLabel('workflow', NodeLabelType.Description);
        expect(result).toEqual('description');
    });


    it('should return the ID as a node label if description is empty', () => {
        const workflowDefinition = cloneDeep(workflowAbstractRepresentation);
        workflowDefinition.workflow.description = '';
        abstractVisualEntity = new WorkflowVisualEntity(workflowDefinition);

        const result = abstractVisualEntity.getNodeLabel('workflow');
        expect(result).toEqual('workflow-8888');
    });
});

describe('getNodeTitle', () => {
    it('should return empty string when path is undefined', () => {
        const result = abstractVisualEntity.getNodeTitle();
        expect(result).toBe('');
    });

    it('should return a capitalized camel title of node', () => {
        const result = abstractVisualEntity.getNodeTitle("workflow");
        expect(result).toEqual("Workflow");
    });

});

describe('getTooltipContent', () => {
    it('should return empty string when path is undefined', () => {
        const result = abstractVisualEntity.getTooltipContent();
        expect(result).toBe('');
    });

    it('should return a description of node node', () => {
        const result = abstractVisualEntity.getTooltipContent("workflow.from");
        expect(result).toEqual("Generate messages in specified intervals using java.util.Timer.");
    });

});

describe('getNodeInteraction', () => {
    it('should not allow marked processors to have previous/next steps', () => {
        const result = abstractVisualEntity.getNodeInteraction({ processorName: 'from' });
        expect(result.canHavePreviousStep).toEqual(false);
        expect(result.canHaveNextStep).toEqual(false);
    });

    it('should allow processors to have previous/next steps', () => {
        const result = abstractVisualEntity.getNodeInteraction({ processorName: 'to' });
        expect(result.canHavePreviousStep).toEqual(true);
        expect(result.canHaveNextStep).toEqual(true);
    });

    it.each([
        'workflow',
        'from',
        'to',
        'log',
        'onException',
        'onCompletion',
        'intercept',
        'interceptFrom',
        'interceptSendToEndpoint',
    ])(`should return the correct interaction for the '%s' processor`, (processorName) => {
        const result = abstractVisualEntity.getNodeInteraction({ processorName });
        expect(result).toMatchSnapshot();
    });
});

describe('getNodeValidationText', () => {
    it('should return an `undefined` if the path is `undefined`', () => {
        const result = abstractVisualEntity.getNodeValidationText(undefined);

        expect(result).toEqual(undefined);
    });

    it('should return an `undefined` if the path is empty', () => {
        const result = abstractVisualEntity.getNodeValidationText('');

        expect(result).toEqual(undefined);
    });

    it('should return a validation text relying on the `validateNodeStatus` method', () => {
        const missingParametersModel = cloneDeep(workflowAbstractRepresentation.workflow);
        missingParametersModel.from.uri = '';
        abstractVisualEntity = new WorkflowVisualEntity(missingParametersModel);

        const result = abstractVisualEntity.getNodeValidationText('route.from');

        expect(result).toEqual('1 required parameter is not yet configured: [ uri ]');
    });
});

describe('getComponentSchema', () => {
    it('should return undefined if path is not provided', () => {
        const result = abstractVisualEntity.getComponentSchema();
        expect(result).toBeUndefined();
    });

    it('should return visualComponentSchema when path is valid', () => {
        const path = 'from.steps.0';
        const visualComponentSchema = {
            schema: {},
            definition: {
                parameters: { some: 'parameter' },
            },
        };

        vi.spyOn(ComponentSchemaService, 'getVisualComponentSchema').mockReturnValue(visualComponentSchema);

        const result = abstractVisualEntity.getComponentSchema(path);
        expect(result).toEqual(visualComponentSchema);
    });

    it('should override parameters with an empty object when parameters is null', () => {
        const path = 'from.steps.0';
        const visualComponentSchema = {
            schema: {},
            definition: {
                parameters: null,
            },
        };

        vi.spyOn(ComponentSchemaService, 'getVisualComponentSchema').mockReturnValue(visualComponentSchema);

        const result = abstractVisualEntity.getComponentSchema(path);
        expect(result?.definition.parameters).toEqual({});
    });

    it.each([undefined, { property: 'value' }])('should not do anything when parameters is not null', (value) => {
        const path = 'from.steps.0';
        const visualComponentSchema = {
            schema: {},
            definition: {
                parameters: value,
            },
        };
        const expected = JSON.parse(JSON.stringify(visualComponentSchema));

        vi.spyOn(ComponentSchemaService, 'getVisualComponentSchema').mockReturnValue(visualComponentSchema);

        const result = abstractVisualEntity.getComponentSchema(path);
        expect(result).toEqual(expected);
    });
});

describe('updateModel', () => {
    it('should update the model with the new value', () => {
        const newUri = 'timer';
        abstractVisualEntity.updateModel('workflow.from', { uri: newUri });

        expect(abstractVisualEntity.entityDef.workflow.from.uri).toEqual(newUri);
    });

    it('should delegate the serialization to the `ComponentSchemaService`', () => {
        const newUri = 'timer';
        const spy = vi.spyOn(ComponentSchemaService, 'getMultiValueSerializedDefinition');
        abstractVisualEntity.updateModel('from', { uri: newUri });

        expect(spy).toHaveBeenCalledWith('from', { uri: newUri });
    });
});

describe('addStep', () => {
    it('should prepend a new step to the model', () => {
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'xchange',
                type: CatalogKind.Component,
                definition: undefined,
            },
            mode: AddStepMode.PrependStep,
            data: {
                path: 'workflow.from.steps.2.to',
                icon: '/src/assets/components/log.svg',
                processorName: 'to',
                componentName: 'log',
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(4);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[2]).toMatchSnapshot();
    });

    it('should append a new step to the model', () => {
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'xchange',
                type: CatalogKind.Component,
                definition: undefined,
            },
            mode: AddStepMode.AppendStep,
            data: {
                path: 'workflow.from.steps.2.to',
                icon: '/src/assets/components/log.svg',
                processorName: 'to',
                componentName: 'log',
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(4);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[3]).toMatchSnapshot();
    });

    it('should replace a step', () => {
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'xchange',
                type: CatalogKind.Component,
                definition: undefined,
            },
            mode: AddStepMode.ReplaceStep,
            data: {
                path: 'workflow.from.steps.0.to',
                icon: '/src/assets/components/log.svg',
                processorName: 'to',
                componentName: 'log',
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(3);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[0]).toMatchSnapshot();
    });

    it('should replace a placeholder step', () => {
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'multicast',
                type: CatalogKind.Processor,
                definition: undefined,
            },
            mode: AddStepMode.ReplaceStep,
            data: {
                path: 'workflow.from.steps.1.choice',
                icon: '/src/assets/components/choice.svg',
                processorName: 'choice',
                componentName: undefined,
            },
        });
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'log',
                type: CatalogKind.Component,
                definition: undefined,
            },
            mode: AddStepMode.ReplaceStep,
            data: {
                isPlaceholder: true,
                path: 'workflow.from.steps.1.multicast.steps.0.placeholder',
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(3);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[1]).toMatchSnapshot();
    });

    it('should insert a new child step', () => {
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'xchange',
                type: CatalogKind.Component,
                definition: undefined,
            },
            mode: AddStepMode.InsertChildStep,
            data: {
                componentName: 'timer',
                icon: '/src/assets/components/timer.svg',
                isGroup: false,
                path: 'workflow.from',
                processorName: 'from',
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(4);
        expect(abstractVisualEntity.entityDef.workflow.from.steps).toMatchSnapshot();
    });

    it('should insert a new special child step belonging to an array like when or doCatch', () => {
        abstractVisualEntity.removeStep('route.from.steps.1.choice.when.0');
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'when',
                type: CatalogKind.Processor,
                definition: undefined,
            },
            mode: AddStepMode.InsertSpecialChildStep,
            data: {
                path: 'workflow.from.steps.1.choice',
                icon: '/src/assets/eip/choice.png',
                processorName: 'choice',
                isGroup: true,
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(3);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[1]).toMatchSnapshot();
    });

    it('should insert a new special child step belonging to a single property like otherwise or doFinally', () => {
        abstractVisualEntity.removeStep('route.from.steps.1.choice.otherwise');
        abstractVisualEntity.addStep({
            definedComponent: {
                name: 'otherwise',
                type: CatalogKind.Processor,
                definition: undefined,
            },
            mode: AddStepMode.InsertSpecialChildStep,
            data: {
                path: 'workflow.from.steps.1.choice',
                icon: '/src/assets/eip/choice.png',
                processorName: 'choice',
                isGroup: true,
            },
        });

        expect(abstractVisualEntity.entityDef.workflow.from.steps).toHaveLength(3);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[1]).toMatchSnapshot();
    });
});

describe('moveNodeTo', () =>{
    it('should return undefined when droppedNodePath is undefined', () => {
        const shallowEntityDef = cloneDeep(abstractVisualEntity.entityDef);
        abstractVisualEntity.moveNodeTo({droppedNodePath:"workflow.from.steps.2.to", draggedNodePath:"workflow.from.steps.0.set-header"});
        expect(abstractVisualEntity.entityDef.workflow.from.steps[2]).toStrictEqual(shallowEntityDef.workflow.from.steps[0]);
        expect(abstractVisualEntity.entityDef.workflow.from.steps[0]).toStrictEqual(shallowEntityDef.workflow.from.steps[1]);
    });

});