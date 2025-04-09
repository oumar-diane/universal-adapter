import {beforeEach, vi, expect, it, describe} from "vitest"
import {isFrom, isWorkflow, WorkflowVisualEntity} from "./workflow-visual-entity.ts";
import {fromJson, fromYaml} from "@/stubs/from.ts";
import {workflowAbstractRepresentation, workflowYaml} from "@/stubs/workflow.ts";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import {cloneDeep} from "lodash";
import {EntityType} from "../model/entity/base-entity.ts";
import {NodeLabelType, SchemaDefinition} from "../model/schema/schema.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";
import {ComponentSchemaService} from "../schema/component-schema-service.ts";

let workflowVisualEntity:WorkflowVisualEntity;

beforeEach(() => {
    workflowVisualEntity = new WorkflowVisualEntity(cloneDeep(workflowAbstractRepresentation));
});


describe('isWorkflow', () => {
    it.each([
        [{ workflow: { from: 'direct:foo' } }, true],
        [{ from: 'direct:foo' }, false],
        [{ from: { uri: 'direct:foo', steps: [] } }, false],
        [fromYaml, true],
        [fromJson, false],
        [undefined, false],
        [null, false],
        [true, false],
        [false, false],
    ])('should mark %s as isWorkflow: %s', (workflow, result) => {
        expect(isWorkflow(workflow)).toEqual(result);
    });
});

describe('isFrom', () => {
    it.each([
        [{ workflow: { from: 'direct:foo' } }, false],
        [{ from: 'direct:foo' }, false],
        [{ from: { uri: 'direct:foo' } }, true],
        [{ from: { uri: 'direct:foo', steps: [] } }, true],
        [workflowYaml, false],
        [fromJson, true],
        [undefined, false],
        [null, false],
        [true, false],
        [false, false],
    ])('should mark %s as isFrom: %s', (workflow, result) => {
        expect(isFrom(workflow)).toEqual(result);
    });
});

describe('id', () => {
    it('should have an uuid', () => {
        expect(workflowVisualEntity.id).toBeDefined();
        expect(typeof workflowVisualEntity.id).toBe('string');
    });

    it('should use a default random id if the workflow id is not provided', () => {
        const workflow = new WorkflowVisualEntity({ from: { uri: 'direct:foo', steps: [] } });

        /** This is being mocked at the window.crypto.get */
        expect(workflow.id).toEqual(expect.any(String));
    });

    it('should have a type', () => {
        expect(workflowVisualEntity.type).toEqual(EntityType.Workflow);
    });

    it('should return the id', () => {
        expect(workflowVisualEntity.getId()).toEqual(expect.any(String));
    });

    it('should change the id', () => {
        workflowVisualEntity.setId('entity-12345');
        expect(workflowVisualEntity.getId()).toEqual('entity-12345');
    });
});

describe('getNodeLabel', () => {
    it('should return an empty string if path is not provided', () => {
        expect(workflowVisualEntity.getNodeLabel()).toEqual('');
    });

    it('should delegate the label lookup to the ComponentSchemaService.getNodeLabel() method', () => {
        const lookupValue = {
            processorName: 'from' as keyof ProcessorDefinition,
            componentName: 'timer',
        };
        const getNodeLabelSpy = vi.spyOn(ComponentSchemaService, 'getNodeLabel');
        vi.spyOn(ComponentSchemaService, 'getComponentLookup').mockReturnValueOnce(lookupValue);

        const label = workflowVisualEntity.getNodeLabel('workflow.from', NodeLabelType.Id);

        expect(getNodeLabelSpy).toHaveBeenCalledWith(lookupValue, workflowAbstractRepresentation.workflow.from, NodeLabelType.Id);
        expect(label).toEqual('timer');
    });
});

describe('getComponentSchema', () => {
    it('should return undefined if no path is provided', () => {
        expect(workflowVisualEntity.getComponentSchema()).toBeUndefined();
    });

    it('should return undefined if no component ' +
        'model is found', () => {
        const result = workflowVisualEntity.getComponentSchema('test');

        expect(result).toEqual({
            schema: {},
            definition: undefined,
        });
    });

    it('should return the component schema', () => {
        const spy = vi.spyOn(ComponentSchemaService, 'getVisualComponentSchema');
        spy.mockReturnValueOnce({
            schema: {} as SchemaDefinition['schema'],
            definition: {},
        });

        workflowVisualEntity.getComponentSchema('workflow.from.uri');

        expect(spy).toHaveBeenCalledWith('workflow.from.uri', "timer");
    });
});

it('should return the json', () => {
    expect(workflowVisualEntity.toJSON()).toEqual({
        workflow: workflowAbstractRepresentation.workflow,
    });
});

describe('updateModel', () => {
    it('should not update the model if no path is provided', () => {
        const originalObject = cloneDeep(workflowAbstractRepresentation.workflow);

        workflowVisualEntity.updateModel(undefined, undefined);

        expect(originalObject).toEqual(workflowAbstractRepresentation.workflow);
    });

    it('should update the model', () => {
        const uri = 'amqp:queue:my-queue';

        workflowVisualEntity.updateModel('workflow.from.uri', uri);

        expect(workflowVisualEntity.entityDef.workflow.from?.uri).toEqual(uri);
    });
});

describe('removeStep', () => {
    it('should not remove any step if no path is provided', () => {
        const originalObject = cloneDeep(workflowAbstractRepresentation);

        workflowVisualEntity.removeStep(undefined);

        expect(originalObject).toEqual(workflowVisualEntity.entityDef);
    });

    it('should set the `workflow.from.uri` property to an empty string if the path is `from`', () => {
        workflowVisualEntity.removeStep('workflow.from');

        expect(workflowVisualEntity.entityDef.workflow.from?.uri).toEqual('');
    });

    it('should remove the step if the path is a number', () => {
        /** Remove `set-header` step */
        workflowVisualEntity.removeStep('workflow.from.steps.0');

        expect(workflowVisualEntity.entityDef.workflow.from?.steps).toHaveLength(2);
        expect(workflowVisualEntity.entityDef.workflow.from?.steps[0].choice).toBeDefined();
    });

    it('should remove the step if the path is a word and the penultimate segment is a number', () => {
        /** Remove `choice` step */
        workflowVisualEntity.removeStep('workflow.from.steps.1.choice');

        expect(workflowVisualEntity.entityDef.workflow.from?.steps).toHaveLength(2);
        expect(workflowVisualEntity.entityDef.workflow.from?.steps[1].to).toBeDefined();
    });

    it('should remove the step if the path is a word and the penultimate segment is a word', () => {
        /** Remove `to` step */
        workflowVisualEntity.removeStep('workflow.from.steps.1.choice.otherwise');

        expect(workflowVisualEntity.entityDef.workflow.from?.steps).toHaveLength(3);
        expect(workflowVisualEntity.entityDef.workflow.from?.steps[1].choice?.otherwise).toBeUndefined();
    });

    it('should remove a nested step', () => {
        /** Remove second `to: amqp` step form the choice.otherwise step */
        workflowVisualEntity.removeStep('workflow.from.steps.1.choice.otherwise.steps.1.to');

        expect(workflowVisualEntity.entityDef.workflow.from?.steps).toHaveLength(3);
        expect(workflowVisualEntity.entityDef.workflow.from?.steps[1].choice?.otherwise?.steps).toHaveLength(2);
    });
});

describe('toVizNode', () => {
    it(`should return the group viz node and set the initial path to '${WorkflowVisualEntity.ROOT_PATH}'`, () => {
        const vizNode = workflowVisualEntity.toVizNode();

        expect(vizNode).toBeDefined();
        expect(vizNode.data.path).toEqual(WorkflowVisualEntity.ROOT_PATH);
    });

    it('should return the group first child and set the initial path to `workflow.from`', () => {
        const vizNode = workflowVisualEntity.toVizNode();
        const fromNode = vizNode.getChildren()?.[0];

        expect(fromNode).toBeDefined();
        expect(fromNode?.data.path).toEqual('workflow.from');
    });

    it('should use the workflow ID as the group label', () => {
        const vizNode = workflowVisualEntity.toVizNode();

        expect(vizNode.getNodeLabel()).toEqual('workflow-8888');
    });

    it('should use the workflow description as the group label if available', () => {
        workflowVisualEntity.entityDef.workflow.description = 'This is a workflow description';
        const vizNode = workflowVisualEntity.toVizNode();

        expect(vizNode.getNodeLabel(NodeLabelType.Description)).toEqual('This is a workflow description');
    });

    it('should use the default group label if the id is not available', () => {
        workflowVisualEntity.entityDef.workflow.id = undefined;
        const vizNode = workflowVisualEntity.toVizNode();

        expect(vizNode.getNodeLabel()).toEqual('workflow-8888');
    });

    it('should use the uri as the node label', () => {
        const vizNode = workflowVisualEntity.toVizNode();
        const fromNode = vizNode.getChildren()?.[0];

        expect(fromNode?.getNodeLabel()).toEqual('timer');
    });

    it('should set a default label if the uri is not available', () => {
        workflowVisualEntity = new WorkflowVisualEntity({ from: {} } as WorkflowDefinition);
        const vizNode = workflowVisualEntity.toVizNode();
        const fromNode = vizNode.getChildren()?.[0];

        expect(fromNode?.getNodeLabel()).toEqual('from: Unknown');
    });

    it('should populate the viz node chain with simple steps', () => {
        const vizNode = new WorkflowVisualEntity({
            workflow: {
                id: 'workflow-1234',
                from: { uri: 'timer', steps: [{ choice: { when: [{ steps: [{ log: { message: 'We got a one.' } }] }] } }] },
            },
        }).toVizNode();
        const fromNode = vizNode.getChildren()![0];

        /** Given a structure of
         * from
         *  - choice
         *    - when
         *      - log
         */

        /** group node */
        expect(vizNode.data.path).toEqual(WorkflowVisualEntity.ROOT_PATH);
        expect(vizNode.data.isGroup).toBeTruthy();
        expect(vizNode.getNodeLabel()).toEqual('workflow-1234');
        /** Since this is the root node, there's no previous step */
        expect(vizNode.getPreviousNode()).toBeUndefined();
        expect(vizNode.getNextNode()).toBeUndefined();

        /** from node and choice group */
        expect(vizNode.getChildren()).toHaveLength(2);
        expect(vizNode.getChildren()?.[0].data.path).toEqual('workflow.from');
        expect(vizNode.getChildren()?.[1].data.path).toEqual('workflow.from.steps.0.choice');

        /** from */
        expect(fromNode.data.path).toEqual('workflow.from');
        expect(fromNode.getNodeLabel()).toEqual('timer');
        /** Since this is the first child node, there's no previous step */
        expect(fromNode.getPreviousNode()).toBeUndefined();
        expect(fromNode.getNextNode()).toBeDefined();
        expect(fromNode.getChildren()).toHaveLength(0);

        /** choice */
        const choiceNode = vizNode.getChildren()?.[1] as IVisualizationNode;
        expect(choiceNode.data.path).toEqual('workflow.from.steps.0.choice');
        expect(choiceNode.getNodeLabel()).toEqual('choice');
        expect(choiceNode.getPreviousNode()).toBe(fromNode);
        expect(choiceNode.getNextNode()).toBeUndefined();
        expect(choiceNode.getChildren()).toHaveLength(1);

        /** choice.when */
        const whenNode = choiceNode.getChildren()?.[0];
        expect(whenNode).toBeDefined();
        expect(whenNode!.data.path).toEqual('workflow.from.steps.0.choice.when.0');
        expect(whenNode!.getNodeLabel()).toEqual('when');
    });

    it('should populate the viz node chain with the steps', () => {
        const vizNode = workflowVisualEntity.toVizNode();
        const fromNode = vizNode.getChildren()![0];

        /** Given a structure of
         * from
         *  - setHeader
         *  - choice
         *    - when
         *      - log
         *   - otherwise
         *    - to
         *    - to
         *    - log
         * - toDirect
         */

        /** group node */
        expect(vizNode.data.path).toEqual(WorkflowVisualEntity.ROOT_PATH);
        expect(vizNode.data.isGroup).toBeTruthy();
        expect(vizNode.getNodeLabel()).toEqual('workflow-8888');
        /** Since this is the root node, there's no previous step */
        expect(vizNode.getPreviousNode()).toBeUndefined();
        expect(vizNode.getNextNode()).toBeUndefined();
        expect(vizNode.getChildren()).toHaveLength(4);

        /** from */
        expect(fromNode.data.path).toEqual('workflow.from');
        expect(fromNode.getNodeLabel()).toEqual('timer');
        /** Since this is the first child node, there's no previous step */
        expect(fromNode.getPreviousNode()).toBeUndefined();
        expect(fromNode.getNextNode()).toBeDefined();
        expect(fromNode.getChildren()).toHaveLength(0);

        /** setHeader */
        const setHeaderNode = vizNode.getChildren()?.[1] as IVisualizationNode;
        expect(setHeaderNode.data.path).toEqual('workflow.from.steps.0.set-header');
        expect(setHeaderNode.getNodeLabel()).toEqual('set-header');
        expect(setHeaderNode.getPreviousNode()).toBe(fromNode);
        expect(setHeaderNode.getNextNode()).toBeDefined();
        expect(setHeaderNode.getChildren()).toBeUndefined();

        /** choice */
        const choiceNode = setHeaderNode.getNextNode()!;
        expect(choiceNode.data.path).toEqual('workflow.from.steps.1.choice');
        expect(choiceNode.getNodeLabel()).toEqual('choice');
        expect(choiceNode.getPreviousNode()).toBe(setHeaderNode);
        expect(choiceNode.getNextNode()).toBeDefined();
        expect(choiceNode.getChildren()).toHaveLength(2);

        /** toDirect */
        const toDirectNode = choiceNode.getNextNode()!;
        expect(toDirectNode.data.path).toEqual('workflow.from.steps.2.to');
        expect(toDirectNode.getNodeLabel()).toEqual('direct');
        expect(toDirectNode.getPreviousNode()).toBe(choiceNode);
        expect(toDirectNode.getNextNode()).toBeUndefined();

        /** choice.when */
        const whenNode = choiceNode.getChildren()?.[0];
        expect(whenNode).toBeDefined();
        expect(whenNode!.data.path).toEqual('workflow.from.steps.1.choice.when.0');
        expect(whenNode!.getNodeLabel()).toEqual('when');
        /** There's no next step since this spawn a new node's tree */
        expect(whenNode!.getPreviousNode()).toBeUndefined();
        expect(whenNode!.getNextNode()).toBeUndefined();
        expect(whenNode!.getParentNode()).toBe(choiceNode);
        expect(whenNode!.getChildren()).toHaveLength(1);

        /** choice.when.log */
        const logWhenNode = whenNode?.getChildren()?.[0];
        expect(logWhenNode).toBeDefined();
        expect(logWhenNode!.data.path).toEqual('workflow.from.steps.1.choice.when.0.steps.0.log');
        expect(logWhenNode!.getNodeLabel()).toEqual('log');
        expect(logWhenNode!.getPreviousNode()).toBeUndefined();
        expect(logWhenNode!.getNextNode()).toBeUndefined();
        expect(logWhenNode!.getParentNode()).toBe(whenNode);
        expect(logWhenNode!.getChildren()).toBeUndefined();

        /** choice.otherwise */
        const otherwiseNode = choiceNode.getChildren()?.[1];
        expect(otherwiseNode).toBeDefined();
        expect(otherwiseNode!.data.path).toEqual('workflow.from.steps.1.choice.otherwise');
        expect(otherwiseNode!.getNodeLabel()).toEqual('otherwise');
        expect(otherwiseNode!.getPreviousNode()).toBeUndefined();
        expect(otherwiseNode!.getNextNode()).toBeUndefined();
        expect(otherwiseNode!.getParentNode()).toBe(choiceNode);
        expect(otherwiseNode!.getChildren()).toHaveLength(3);

        /** choice.otherwise.to 1st */
        const firstToOtherwiseNode = otherwiseNode?.getChildren()?.[0];
        expect(firstToOtherwiseNode).toBeDefined();
        expect(firstToOtherwiseNode!.data.path).toEqual('workflow.from.steps.1.choice.otherwise.steps.0.to');
        expect(firstToOtherwiseNode!.getNodeLabel()).toEqual('amqp');
        expect(firstToOtherwiseNode!.getPreviousNode()).toBeUndefined();
        expect(firstToOtherwiseNode!.getNextNode()).toBeDefined();
        expect(firstToOtherwiseNode!.getParentNode()).toBe(otherwiseNode);
        expect(firstToOtherwiseNode!.getChildren()).toBeUndefined();

        /** choice.otherwise.to 2nd*/
        const secondToOtherwiseNode = otherwiseNode?.getChildren()?.[1];
        expect(secondToOtherwiseNode).toBeDefined();
        expect(secondToOtherwiseNode!.data.path).toEqual('workflow.from.steps.1.choice.otherwise.steps.1.to');
        expect(secondToOtherwiseNode!.getNodeLabel()).toEqual('amqp');
        expect(secondToOtherwiseNode!.getPreviousNode()).toBe(firstToOtherwiseNode);
        expect(secondToOtherwiseNode!.getNextNode()).toBeDefined();
        expect(secondToOtherwiseNode!.getParentNode()).toBe(otherwiseNode);
        expect(secondToOtherwiseNode!.getChildren()).toBeUndefined();

        /** choice.otherwise.log */
        const logOtherwiseNode = otherwiseNode?.getChildren()?.[2];
        expect(logOtherwiseNode).toBeDefined();
        expect(logOtherwiseNode!.data.path).toEqual('workflow.from.steps.1.choice.otherwise.steps.2.log');
        expect(logOtherwiseNode!.getNodeLabel()).toEqual('log');
        expect(logOtherwiseNode!.getPreviousNode()).toBe(secondToOtherwiseNode);
        expect(logOtherwiseNode!.getNextNode()).toBeUndefined();
        expect(logOtherwiseNode!.getParentNode()).toBe(otherwiseNode);
        expect(logOtherwiseNode!.getChildren()).toBeUndefined();
    });
});