import {ProcessorDefinition} from "@/@types";
import {ParallelProcessorBaseNodeMapper} from "./parallel-processor-base-node-mapper.ts";

export class LoadBalanceNodeMapper extends ParallelProcessorBaseNodeMapper {
    getProcessorName(): keyof ProcessorDefinition {
        return 'loadBalance';
    }
}
