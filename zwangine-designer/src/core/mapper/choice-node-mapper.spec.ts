import {beforeEach, it, expect} from "vitest"
import { ChoiceNodeMapper } from "./choice-node-mapper";
import {ProcessorDefinition, WorkflowDefinition} from "@/@types";
import { RootNodeMapper } from "./root-node-mapper";
import { WhenNodeMapper } from "./when-node-mapper";
import { OtherwiseNodeMapper } from "./otherwise-node-mapper";
import {noopNodeMapper} from "@/core/mapper/noop-node-mapper.ts";

let mapper: ChoiceNodeMapper;
let routeDefinition: WorkflowDefinition;
const path = 'from.steps.0.choice';

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    const whenNodeMapper = new WhenNodeMapper(rootNodeMapper);
    const otherwiseNodeMapper = new OtherwiseNodeMapper(rootNodeMapper);
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('when' as keyof ProcessorDefinition, whenNodeMapper);
    rootNodeMapper.registerMapper('otherwise' as keyof ProcessorDefinition, otherwiseNodeMapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);

    mapper = new ChoiceNodeMapper(rootNodeMapper);

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
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'choice' }, routeDefinition);

    expect(vizNode.getChildren()).toHaveLength(3);
});

it('should return `when` nodes as children', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'choice' }, routeDefinition);

    expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.choice.when.0');
    expect(vizNode.getChildren()?.[1].data.path).toBe('from.steps.0.choice.when.1');
});

it('should return an `otherwise` node if defined', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'choice' }, routeDefinition);

    expect(vizNode.getChildren()?.[2].data.path).toBe('from.steps.0.choice.otherwise');
});

it('should not return an `otherwise` node if not defined', () => {
    routeDefinition.from.steps[0].choice!.otherwise = undefined;

    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'choice' }, routeDefinition);

    expect(vizNode.getChildren()).toHaveLength(2);
    expect(vizNode.getChildren()?.[0].data.path).toBe('from.steps.0.choice.when.0');
    expect(vizNode.getChildren()?.[1].data.path).toBe('from.steps.0.choice.when.1');
});