import {CatalogDefinitionEntry} from "@/@types/catalog-index";
import {SchemaDefinition} from "@/core";

export class CatalogSchemaLoader {
    /** The `.` is required to support relative routes in GitHub pages */
    static readonly DEFAULT_CATALOG_PATH = '/resources';
    static readonly VISUAL_FLOWS = ['workflow', 'Integration', 'Kamelet', 'KameletBinding', 'Pipe'];

    static async fetchFile<T>(file: string): Promise<{ body: T; uri: string }> {
        const response = await fetch(file);
        const body = await response.json();

        return { body, uri: response.url };
    }

    static getSchemasFiles(
        schemaFiles: Record<string, CatalogDefinitionEntry>,
    ): Promise<SchemaDefinition>[] {
        return Object.entries(schemaFiles).map(async ([name, schemaDef]) => {
            const file = `${this.DEFAULT_CATALOG_PATH}/${schemaDef.file}`;
            const fetchedSchema = await this.fetchFile(file);
            const tags = [];

            if (this.VISUAL_FLOWS.includes(name)) {
                tags.push('visualization');
            }

            return {
                name,
                tags,
                uri: fetchedSchema.uri,
                schema: fetchedSchema.body as SchemaDefinition['schema'],
            };
        });
    }

    static getRelativeBasePath(catalogIndexFile: string): string {
        return catalogIndexFile.substring(0, catalogIndexFile.lastIndexOf('/'));
    }
}
