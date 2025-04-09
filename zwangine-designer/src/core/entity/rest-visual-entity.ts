import {ProcessorDefinition, Rest} from '@/@types';
import { NodeIconResolver, NodeIconType, getValue, isDefined, setValue } from '@/lib/utility';
import { SchemaService } from '@/components/form/schema-service.ts';
import { getRandomId } from '@/lib/ua-utils';
import { AbstractVisualEntity } from './abstract-visual-entity.ts';
import { CatalogService } from '../catalog/catalog-service.ts';
import { ComponentFilterService } from '../component-filter-service.ts';
import {NodeMapperService} from "../mapper/node-mapper-service.ts";
import {
    BaseVisualEntity,
    IVisualizationNode,
    IVisualizationNodeData,
    NodeInteraction,
    VisualComponentSchema
} from '../model/entity/base-visual-entity.ts';
import {EntityType} from "../model/entity/base-entity.ts";
import { CatalogKind } from '../model/catalog/catalog-kind.ts';

export class RestVisualEntity extends AbstractVisualEntity<{ rest: Rest }> implements BaseVisualEntity {
    id: string;
    readonly type = EntityType.Rest;
    static readonly ROOT_PATH = 'rest';
    private readonly OMIT_FORM_FIELDS = [
        ...SchemaService.OMIT_FORM_FIELDS,
        'get',
        'post',
        'put',
        'delete',
        'head',
        'patch',
    ];

    constructor(public restDef: { rest: Rest } = { rest: {} }) {
        super(restDef);
        const id = restDef.rest.id ?? getRandomId(RestVisualEntity.ROOT_PATH);
        this.id = id;
        this.restDef.rest.id = id;
    }

    static isApplicable(restDef: unknown): restDef is { rest: Rest } {
        if (!isDefined(restDef) || Array.isArray(restDef) || typeof restDef !== 'object') {
            return false;
        }

        const objectKeys = Object.keys(restDef!);

        return objectKeys.length === 1 && this.ROOT_PATH in restDef! && typeof restDef.rest === 'object';
    }

    getRootPath() {
        return RestVisualEntity.ROOT_PATH;
    }

    setId(id: string): void {
        this.id = id;
    }

    getComponentSchema(path?: string): VisualComponentSchema | undefined {
        if (path === RestVisualEntity.ROOT_PATH) {
            return {
                definition: Object.assign({}, this.restDef.rest),
                schema: CatalogService.getComponent(CatalogKind.Entity, 'rest')?.propertiesSchema ?? {},
            };
        }

        /** If we're targetting a Rest method, the path would be `rest.get.0` */
        const method = path?.split('.')[1] ?? '';
        if (isDefined(path) && ComponentFilterService.REST_DSL_METHODS.includes(method)) {
            return {
                definition: Object.assign({}, getValue(this.restDef, path)),
                schema: CatalogService.getComponent(CatalogKind.Pattern, method)?.propertiesSchema ?? {},
            };
        }

        return super.getComponentSchema(path);
    }

    getOmitFormFields(): string[] {
        return this.OMIT_FORM_FIELDS;
    }

    updateModel(path: string | undefined, value: unknown): void {
        if (!path) return;

        setValue(this.restDef, path, value);

        if (!isDefined(this.restDef.rest)) {
            this.restDef.rest = {};
        }
    }

    getNodeInteraction(): NodeInteraction {
        return {
            canHavePreviousStep: false,
            canHaveNextStep: false,
            canHaveChildren: false,
            /** Replace it with `true` when enabling the methods (GET, POST, PUT) */
            canHaveSpecialChildren: false,
            canRemoveStep: false,
            canReplaceStep: false,
            canRemoveFlow: true,
            canBeDisabled: true,
        };
    }

    getNodeValidationText(): string | undefined {
        return undefined;
    }

    toVizNode(): IVisualizationNode<IVisualizationNodeData> {
        const restGroupNode = NodeMapperService.getVizNode(
            this.getRootPath(),
            { processorName: 'rest' as keyof ProcessorDefinition },
            this.restDef,
        );
        restGroupNode.data.entity = this;
        restGroupNode.data.isGroup = true;
        restGroupNode.data.icon = NodeIconResolver.getIcon(this.type, NodeIconType.Entity);

        return restGroupNode;
    }

    toJSON(): { rest: Rest } {
        return { rest: this.restDef.rest };
    }

    protected getRootUri(): string | undefined {
        return undefined;
    }
}
