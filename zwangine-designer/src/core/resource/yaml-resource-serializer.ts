import { parse, stringify } from 'yaml';
import {isXML} from "../utils.ts";
import { YamlDsl } from '@/@types/yaml-dsl';
import {Resource} from "../model/resource/resource.ts";
import {ResourceSerializer, SerializerType} from "../model/resource/resource-serializer.ts";

export class YamlResourceSerializer implements ResourceSerializer {
    /**
     * Regular expression to match commented lines, regardless of indentation
     * Given the following examples, the regular expression should match the comments:
     * ```
     * # This is a comment
     *     # This is an indented comment
     *# This is an indented comment
     * ```
     * The regular expression should match the first three lines
     */
    static readonly COMMENTED_LINES_REGEXP = /^\s*#.*$/;
    comments: string[] = [];
    metadata: string = '';

    static isApplicable(code: unknown): boolean {
        return !isXML(code);
    }

    parse(code: string): YamlDsl{
        if (!code || typeof code !== 'string') return [];

        this.comments = this.parseComments(code);
        const json = parse(code);
        return json;
    }

    serialize(resource: Resource): string {
        let code = stringify(resource, { sortMapEntries: resource.sortFn, schema: 'yaml-1.1' }) || '';
        if (this.comments.length > 0) {
            code = this.insertComments(code);
        }
        return code;
    }

    getType(): SerializerType {
        return SerializerType.YAML;
    }

    getComments(): string[] {
        return this.comments;
    }

    setComments(comments: string[]): void {
        this.comments = comments;
    }

    setMetadata(metadata: string): void {
        this.metadata = metadata;
    }

    getMetadata(): string {
        return this.metadata;
    }

    private parseComments(code: string): string[] {
        const lines = code.split('\n');
        const comments: string[] = [];
        for (const line of lines) {
            if (line.trim() === '' || YamlResourceSerializer.COMMENTED_LINES_REGEXP.test(line)) {
                comments.push(line.replace(/^\s*#*/, ''));
            } else {
                break;
            }
        }
        return comments;
    }

    private insertComments(xml: string): string {
        const commentsString = this.comments
            .flatMap((comment) => comment.split('\n').map((line) => (line.trim() === '' ? '' : `#${line}`)))
            .join('\n');
        return commentsString + '\n' + xml;
    }
}
