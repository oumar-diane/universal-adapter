import {describe, expect, it, vi} from "vitest"
import { RootNodeMapper } from "./root-node-mapper.ts";
import { noopNodeMapper } from "./noop-node-mapper.ts";

it('should allow consumers to register mappers', () => {
    const rootNodeMapper = new RootNodeMapper();

    rootNodeMapper.registerMapper('log', noopNodeMapper);

    expect(() => rootNodeMapper.getVizNodeFromProcessor('path', { processorName: 'log' }, {})).not.toThrow();
});

it('should allow consumers to register a default mapper', () => {
    const rootNodeMapper = new RootNodeMapper();

    rootNodeMapper.registerDefaultMapper(noopNodeMapper);

    expect(() => rootNodeMapper.getVizNodeFromProcessor('path', { processorName: 'log' }, {})).not.toThrow();
});

it('should throw an error when no mapper is found', () => {
    const rootNodeMapper = new RootNodeMapper();

    expect(() => rootNodeMapper.getVizNodeFromProcessor('path', { processorName: 'log' }, {})).toThrow(
        'No mapper found for processor: log',
    );
});

describe('getVizNodeFromProcessor', () => {
    it('should delegate to the default mapper when no mapper is found', () => {
        const rootNodeMapper = new RootNodeMapper();
        rootNodeMapper.registerDefaultMapper(noopNodeMapper);
        vi.spyOn(noopNodeMapper, 'getVizNodeFromProcessor');

        const vizNode = rootNodeMapper.getVizNodeFromProcessor('path', { processorName: 'log' }, {});

        expect(noopNodeMapper.getVizNodeFromProcessor).toHaveBeenCalledWith('path', { processorName: 'log' }, {});
        expect(vizNode).toBeDefined();
    });

    it('should delegate to the registered mapper', () => {
        const rootNodeMapper = new RootNodeMapper();
        rootNodeMapper.registerMapper('log', noopNodeMapper);
        vi.spyOn(noopNodeMapper, 'getVizNodeFromProcessor');

        const vizNode = rootNodeMapper.getVizNodeFromProcessor('path', { processorName: 'log' }, {});

        expect(noopNodeMapper.getVizNodeFromProcessor).toHaveBeenCalledWith('path', { processorName: 'log' }, {});
        expect(vizNode).toBeDefined();
    });
});