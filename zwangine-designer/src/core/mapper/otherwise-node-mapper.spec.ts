import { beforeEach, expect, it } from "vitest"
import { OtherwiseNodeMapper } from "./otherwise-node-mapper";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import { RootNodeMapper } from "./root-node-mapper";
import {noopNodeMapper} from "./noop-node-mapper.ts";

let mapper: OtherwiseNodeMapper;
let routeDefinition: WorkflowDefinition;
const path = 'from.steps.0.choice.otherwise';

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);

    mapper = new OtherwiseNodeMapper(rootNodeMapper);

    routeDefinition = {
        from: {
            uri: 'timer',
            parameters: {
                timerName: 'timerName',
            },
            steps: [
                {
                    choice: {
                        when: [{ simple: "${header.foo} == 'bar'" }, { simple: "${header.foo} == 'baz'" }],
                        otherwise: {
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
        { processorName: 'otherwise' as keyof ProcessorDefinition },
        routeDefinition,
    );

    expect(vizNode.getChildren()).toHaveLength(1);
});