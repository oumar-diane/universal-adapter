import { Card, CardBody, CardFooter, CardHeader, CardTitle, LabelGroup } from '@patternfly/react-core';
import { FunctionComponent, PropsWithChildren, useCallback } from 'react';
import './tile.scss';
import { ITile } from './catalog-models';
import {IconResolver} from "@/components/iconResolver/icon-resolver.tsx";
import {CatalogTagsPanel, CatalogTag} from "@/components/catalog";

interface TileProps {
    tile: ITile;
    onClick: (tile: ITile) => void;
    onTagClick: (_event: unknown, value: string) => void;
}

export const Tile: FunctionComponent<PropsWithChildren<TileProps>> = (props) => {
    const onTileClick = useCallback(() => {
        props.onClick(props.tile);
    }, [props]);

    return (
        <Card
            className="tile"
            data-testid={'tile-' + props.tile.name}
            isClickable
            isCompact
            role="button"
            key={props.tile.name}
            id={props.tile.name}
        >
            <CardHeader
                selectableActions={{ variant: 'single', selectableActionAriaLabelledby: `Selectable ${props.tile.name}` }}
                data-testid={'tile-header-' + props.tile.name}
                onClick={onTileClick}
            >
                <div className="tile__header">
                    <IconResolver className="tile__icon" tile={props.tile} />
                    <LabelGroup isCompact aria-label="tile-headers-tags">
                        {props.tile.headerTags?.map((tag, index) => (
                            <CatalogTag key={`${props.tile.name}-${tag}-${index}`} tag={tag} />
                        ))}
                    </LabelGroup>
                </div>

                <CardTitle className="tile__title">
                    <span>{props.tile.title}</span>
                    <span className="tile__name">({props.tile.name})</span>
                    {props.tile.version && (
                        <CatalogTag key={`${props.tile.version}`} tag={props.tile.version} variant="outline" />
                    )}
                </CardTitle>
            </CardHeader>

            <CardBody className="tile__body">{props.tile.description}</CardBody>

            <CardFooter>
                <CatalogTagsPanel tags={props.tile.tags} onTagClick={props.onTagClick} />
            </CardFooter>

        </Card>
    );
};
