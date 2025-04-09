import {ProcessorDefinition} from "@/@types";
import {ParallelProcessorBaseNodeMapper} from "./parallel-processor-base-node-mapper.ts";

export class MulticastNodeMapper extends ParallelProcessorBaseNodeMapper {
    getProcessorName(): keyof ProcessorDefinition {
        return 'multicast';
    }
}
