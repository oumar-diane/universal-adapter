import {beforeEach, it , expect} from "vitest"
import { WhenNodeMapper } from "./when-node-mapper";
import { RootNodeMapper } from "./root-node-mapper";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import {noopNodeMapper} from "./noop-node-mapper.ts";

let mapper: WhenNodeMapper;
let workflowDefinition: WorkflowDefinition;
const path = 'from.steps.0.choice.when.0';

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);

    mapper = new WhenNodeMapper(rootNodeMapper);

    workflowDefinition = {
        from: {
            uri: 'timer',
            parameters: {
                timerName: 'timerName',
            },
            steps: [
                {
                    choice: {
                        when: [
                            { expression: { simple: { expression: "${header.foo} == 'bar'" } }, steps: [{ log: 'logName' }] },
                            { expression: { simple: { expression: "${header.foo} == 'baz'" } }, steps: [{ log: 'logName' }] },
                        ],
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
        { processorName: 'when' as keyof ProcessorDefinition },
        workflowDefinition,
    );

    expect(vizNode.getChildren()).toHaveLength(1);
});