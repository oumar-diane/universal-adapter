import {it, vi, expect } from "vitest"
import { RootNodeMapper} from "./root-node-mapper";
import { NodeMapperService } from "./node-mapper-service";
import { BaseNodeMapper } from "./base-node-mapper";
import { CircuitBreakerNodeMapper } from "./circuit-breaker-node-mapper";
import {OnFallbackNodeMapper} from "./on-fallback-node-mapper.ts";
import { ChoiceNodeMapper } from "./choice-node-mapper.ts";
import {OtherwiseNodeMapper} from "./otherwise-node-mapper.ts";
import {WhenNodeMapper} from "./when-node-mapper.ts";
import {StepNodeMapper} from "./step-node-mapper.ts";
import {DATAMAPPER_ID_PREFIX} from "@/lib/utility";
import {DataMapperNodeMapper} from "./datamapper-node-mapper.ts";


it('should initialize the root node mapper', () => {
    const registerDefaultMapperSpy = vi.spyOn(RootNodeMapper.prototype, 'registerDefaultMapper');
    const registerMapperSpy = vi.spyOn(RootNodeMapper.prototype, 'registerMapper');

    NodeMapperService.getVizNode('path', { processorName: 'log' }, {});

    expect(registerDefaultMapperSpy).toHaveBeenCalledWith(expect.any(BaseNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('circuitBreaker', expect.any(CircuitBreakerNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('onFallback', expect.any(OnFallbackNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('choice', expect.any(ChoiceNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('when', expect.any(WhenNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('otherwise', expect.any(OtherwiseNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith('step', expect.any(StepNodeMapper));
    expect(registerMapperSpy).toHaveBeenCalledWith(DATAMAPPER_ID_PREFIX, expect.any(DataMapperNodeMapper));
});