import { beforeEach, it, expect} from "vitest"
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import { CircuitBreakerNodeMapper } from "./circuit-breaker-node-mapper";
import { RootNodeMapper } from "./root-node-mapper";
import {OnFallbackNodeMapper} from "./on-fallback-node-mapper.ts";
import {noopNodeMapper} from "@/core/mapper/noop-node-mapper.ts";


let mapper: CircuitBreakerNodeMapper;
let routeDefinition: WorkflowDefinition;
const path = 'from.steps.0.circuitBreaker';

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    const onFallbackNodeMapper = new OnFallbackNodeMapper(rootNodeMapper);
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('onFallback' as keyof ProcessorDefinition, onFallbackNodeMapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);

    mapper = new CircuitBreakerNodeMapper(rootNodeMapper);

    routeDefinition = {
        from: {
            uri: 'timer',
            parameters: {
                timerName: 'timerName',
            },
            steps: [
                {
                    circuitBreaker: {
                        steps: [{ log: 'step log' }],
                        onFallback: {
                            steps: [{ log: 'onFallback log' }],
                        },
                    },
                },
            ],
        },
    };
});

it('should return children', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'circuitBreaker' }, routeDefinition);

    expect(vizNode.getChildren()).toHaveLength(2);
});

it('should return step nodes as children', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'circuitBreaker' }, routeDefinition);

    expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.circuitBreaker.steps.0.log');
});

it('should return an `onFallback` node if defined', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'circuitBreaker' }, routeDefinition);

    expect(vizNode.getChildren()?.[1].data.path).toBe('from.steps.0.circuitBreaker.onFallback');
});

it('should not return an `onFallback` node if not defined', () => {
    routeDefinition.from.steps[0].circuitBreaker!.onFallback = undefined;

    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'circuitBreaker' }, routeDefinition);

    expect(vizNode.getChildren()).toHaveLength(1);
    expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.circuitBreaker.steps.0.log');
});