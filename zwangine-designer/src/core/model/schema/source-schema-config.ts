import {SchemaDefinition, SourceSchemaType} from "./schema.ts";
import {isEnumType} from "@/lib/utility";

export interface ISourceSchema {
    schema: SchemaDefinition | undefined;
    name: string;
    multipleWorkflow: boolean;
    description?: string;
}

interface IEntitySchemaConfig {
    [SourceSchemaType.Workflow]: ISourceSchema;
}

class SourceSchemaConfig {
    config: IEntitySchemaConfig = {
        [SourceSchemaType.Workflow]: {
            name: 'Universal Adapters Workflow',
            schema: undefined,
            multipleWorkflow: true,
            description:
                'Defines an executable integration flow by declaring a source (starter) and followed by a sequence of actions (or steps). Actions can include data manipulations, EIPs (integration patterns) and internal or external calls.',
        },
    };

    setSchema(name: string, schema: SchemaDefinition) {
        if (name === 'yamlDsl') {
            this.config[SourceSchemaType.Workflow].schema = schema;
        }
        if (isEnumType(name, SourceSchemaType)) {
            const type: SourceSchemaType = SourceSchemaType[name];
            this.config[type].schema = schema;
        }
    }
}

export const sourceSchemaConfig = new SourceSchemaConfig();
