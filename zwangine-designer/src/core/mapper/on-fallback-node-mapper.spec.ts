import { beforeEach, it, expect} from "vitest"
import { OnFallbackNodeMapper } from "./on-fallback-node-mapper";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import { RootNodeMapper } from "./root-node-mapper";
import { noopNodeMapper } from "./noop-node-mapper";

let mapper: OnFallbackNodeMapper;
let routeDefinition: WorkflowDefinition;
const path = 'from.steps.0.choice.onFallback';

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);

    mapper = new OnFallbackNodeMapper(rootNodeMapper);

    routeDefinition = {
        from: {
            uri: 'timer',
            parameters: {
                timerName: 'timerName',
            },
            steps: [
                {
                    circuitBreaker: {
                        onFallback: {
                            steps: [{ log: 'logName' }],
                        },
                    },
                },
            ],
        },
    };
});

it('should return children', () => {
    const vizNode = mapper.getVizNodeFromProcessor(
        path,
        { processorName: 'onFallback' as keyof ProcessorDefinition },
        routeDefinition,
    );

    expect(vizNode.getChildren()).toHaveLength(1);
});