import {YamlDsl} from "@/@types";
import {Resource} from "@/core/model/resource/resource.ts";

export enum SerializerType {
    YAML = 'YAML',
}

export interface ResourceSerializer {
    parse: (code: string) => YamlDsl | undefined;
    serialize: (resource: Resource) => string;
    getComments: () => string[];
    setComments: (comments: string[]) => void;
    setMetadata: (metadata: string) => void;
    getMetadata: () => string;
    getType(): SerializerType;
}
