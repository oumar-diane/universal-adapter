import {YamlDsl} from "@/@types";
import {Resource} from "../model/resource/resource.ts";
import {SourceSchemaType} from "../model/schema/schema.ts";
import {ResourceSerializer} from "../model/resource/resource-serializer.ts";
import {getResourceTypeFromPath} from "../schema/source-schema-type.ts";
import {WorkflowResource} from "./workflow-resource.ts";
import {YamlResourceSerializer} from "./yaml-resource-serializer.ts";

export class ResourceFactory {
    /**
     * Creates a Resource based on the given {@link type} and {@link source}. If
     * both are not specified, a default empty {@link WorkflowResource} is created.
     * If only {@link type} is specified, an empty {@link Resource} of the given
     * {@link type} is created.
     * @param source
     * @param options
     */
    static createResource(source?: string,  options: Partial<{ path: string }> = {}): Resource {
        const pathResourceType = getResourceTypeFromPath(options.path)
        const serializer = this.initSerializer();
        const parsedCode = typeof source === 'string' ? serializer.parse(source) : source;
        if(!(pathResourceType === SourceSchemaType.Workflow)){
            throw new Error("Resource type not supported");
        }
        return new WorkflowResource(parsedCode as YamlDsl, serializer);
    }

    private static initSerializer(): ResourceSerializer {
        return new YamlResourceSerializer();
    }
}
