import { DATAMAPPER_ID_PREFIX } from '@/lib/utility';
import { RootNodeMapper } from './root-node-mapper.ts';
import {IElementLookupResult} from "../model/component-type.ts";
import {IVisualizationNode} from "../model/entity/base-visual-entity.ts";
import {INodeMapper} from "../model/mapper/node-mapper.ts";
import { BaseNodeMapper } from './base-node-mapper.ts';
import { CircuitBreakerNodeMapper } from './circuit-breaker-node-mapper.ts';
import { OnFallbackNodeMapper } from './on-fallback-node-mapper.ts';
import {ChoiceNodeMapper} from "./choice-node-mapper.ts";
import {WhenNodeMapper} from "./when-node-mapper.ts";
import {OtherwiseNodeMapper} from "./otherwise-node-mapper.ts";
import {DataMapperNodeMapper} from "./datamapper-node-mapper.ts";
import {MulticastNodeMapper} from "./multicast-node-mapper.ts";
import {LoadBalanceNodeMapper} from "./loadbalance-node-mapper.ts";
import {StepNodeMapper} from "./step-node-mapper.ts";


export class NodeMapperService {
    private static rootNodeMapper: RootNodeMapper;

    static getVizNode(
        path: string,
        componentLookup: IElementLookupResult,
        entityDefinition: unknown,
    ): IVisualizationNode {
        return this.getInstance().getVizNodeFromProcessor(path, componentLookup, entityDefinition);
    }

    private static getInstance(): INodeMapper {
        if (!this.rootNodeMapper) {
            NodeMapperService.initializeRootNodeMapper();
        }

        return this.rootNodeMapper;
    }

    private static initializeRootNodeMapper() {
        this.rootNodeMapper = new RootNodeMapper();
        this.rootNodeMapper.registerDefaultMapper(new BaseNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('circuitBreaker', new CircuitBreakerNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('onFallback', new OnFallbackNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('choice', new ChoiceNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('when', new WhenNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('otherwise', new OtherwiseNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('step', new StepNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper(DATAMAPPER_ID_PREFIX, new DataMapperNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('multicast', new MulticastNodeMapper(this.rootNodeMapper));
        this.rootNodeMapper.registerMapper('loadBalance', new LoadBalanceNodeMapper(this.rootNodeMapper));
    }
}
