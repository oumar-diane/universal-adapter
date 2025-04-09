import {beforeEach, it, expect, vi} from "vitest"
import { StepNodeMapper } from "./step-node-mapper";
import { WorkflowDefinition } from "@/@types";
import { RootNodeMapper } from "./root-node-mapper";
import { noopNodeMapper } from "./noop-node-mapper";
import { DATAMAPPER_ID_PREFIX } from "@/lib/utility";
import {parse} from "yaml";
import {DataMapperNodeMapper} from "./datamapper-node-mapper.ts";

let mapper: StepNodeMapper;
let routeDefinition: WorkflowDefinition;
let rootNodeMapper: RootNodeMapper;
const path = 'from.steps.0.step';

beforeEach(() => {
    rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);
    rootNodeMapper.registerMapper('log', noopNodeMapper);
    rootNodeMapper.registerMapper(DATAMAPPER_ID_PREFIX, noopNodeMapper);

    mapper = new StepNodeMapper(rootNodeMapper);

    routeDefinition = parse(`
      from:
        id: from-8888
        uri: direct:start
        parameters: {}
        steps:
          - step:
              id: step-1234
              steps:
                - log:
                    id: log-1234
                    message: \${body}`);
});

it('should return children', () => {
    const vizNode = mapper.getVizNodeFromProcessor(path, { processorName: 'step' }, routeDefinition);

    expect(vizNode.getChildren()).toHaveLength(1);
});

it('should delegate to the rootNodeMapper if this step node is a DataMapper one', () => {
    const rootNodeMapperSpy = vi.spyOn(rootNodeMapper, 'getVizNodeFromProcessor');
    const dataMapperNodeSpy = vi.spyOn(DataMapperNodeMapper, 'isDataMapperNode');
    dataMapperNodeSpy.mockReturnValue(true);

    mapper.getVizNodeFromProcessor(path, { processorName: 'step' }, routeDefinition);

    expect(rootNodeMapperSpy).toHaveBeenCalledWith(path, { processorName: DATAMAPPER_ID_PREFIX }, routeDefinition);
});