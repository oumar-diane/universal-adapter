import { beforeEach, it, expect} from "vitest"
import { MulticastNodeMapper } from "./multicast-node-mapper";
import {RootNodeMapper} from "./root-node-mapper.ts";

let mapper: MulticastNodeMapper;

beforeEach(() => {
    const rootNodeMapper = new RootNodeMapper();
    rootNodeMapper.registerDefaultMapper(mapper);

    mapper = new MulticastNodeMapper(rootNodeMapper);
});

it('should return "multicast" as the processor name', () => {
    expect(mapper.getProcessorName()).toBe('multicast');
});