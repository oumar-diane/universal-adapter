import { BaseNodeMapper } from './base-node-mapper.ts';
import {beforeEach, describe, expect, it} from 'vitest';
import {RootNodeMapper} from "./root-node-mapper.ts";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import {IElementLookupResult} from "../model/component-type.ts";

let mapper: BaseNodeMapper;
let path: string;
let componentLookup: IElementLookupResult;
let entityDefinition: unknown;

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    mapper = new BaseNodeMapper(rootNodeMapper);
    rootNodeMapper.registerDefaultMapper(mapper);

    path = 'from';
    componentLookup = {
        processorName: 'from' as keyof ProcessorDefinition,
        componentName: 'timer',
    };
    entityDefinition = { uri: 'timer', parameters: { timerName: 'timerName' }, steps: [] };
});

describe('getVizNodeFromProcessor', () => {
    it('should return a VisualizationNode', () => {
        const vizNode = mapper.getVizNodeFromProcessor(path, componentLookup, entityDefinition);

        expect(vizNode).toBeDefined();
        expect(vizNode.data).toMatchObject({
            path,
            icon: expect.any(String),
            processorName: 'from',
            componentName: 'timer',
        });
    });

    it('should return a VisualizationNode with children', () => {
        const workflowDefinition: WorkflowDefinition = {
            from: {
                uri: 'timer',
                parameters: {
                    timerName: 'timerName',
                },
                steps: [{ log: 'logName' }, { to: 'direct:anotherRoute' }],
            },
        };

        const vizNode = mapper.getVizNodeFromProcessor(path, componentLookup, workflowDefinition);
        expect(vizNode.getChildren()).toHaveLength(2);
        expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.log');
        expect(vizNode.getChildren()?.[1].data.path).toBe('from.steps.1.to');
    });

    it('should return a VisualizationNode with special children', () => {
        const workflowDefinition: WorkflowDefinition = {
            from: {
                uri: 'timer',
                parameters: {
                    timerName: 'timerName',
                },
                steps: [
                    {
                        doTry: {
                            doCatch: [{ exception: ['java.lang.RuntimeException'] }, { exception: ['java.lang.RuntimeException'] }],
                            doFinally: { steps: [{ log: 'logName' }] },
                        },
                    },
                ],
            },
        };

        const vizNode = mapper.getVizNodeFromProcessor(path, componentLookup, workflowDefinition);
        expect(vizNode.getChildren()).toHaveLength(1);
        expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.doTry');

        const doTryNode = vizNode.getChildren()?.[0];
        expect(doTryNode?.getChildren()).toHaveLength(4);
        expect(doTryNode?.getChildren()?.[0].data.path).toBe('from.steps.0.doTry.steps.0.placeholder');
        expect(doTryNode?.getChildren()?.[1].data.path).toBe('from.steps.0.doTry.doCatch.0');
        expect(doTryNode?.getChildren()?.[2].data.path).toBe('from.steps.0.doTry.doCatch.1');
        expect(doTryNode?.getChildren()?.[3].data.path).toBe('from.steps.0.doTry.doFinally');
    });
});