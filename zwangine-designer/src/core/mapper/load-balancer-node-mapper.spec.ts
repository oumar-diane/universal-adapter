import {beforeEach, it, expect} from "vitest"
import { LoadBalanceNodeMapper } from "./loadbalance-node-mapper";
import {RootNodeMapper} from "./root-node-mapper.ts";

let mapper: LoadBalanceNodeMapper;

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);

    mapper = new LoadBalanceNodeMapper(rootNodeMapper);
});

it('should return "loadBalance" as the processor name', () => {
    expect(mapper.getProcessorName()).toBe('loadBalance');
});