import { FunctionComponent, PropsWithChildren } from 'react';
import {ITile} from "@/components/catalog";
import { CatalogKind } from '@/core';
import {NodeIconResolver, NodeIconType} from "@/lib/utility";

interface IconResolverProps {
    className?: string;
    tile: ITile;
}

export const IconResolver: FunctionComponent<PropsWithChildren<IconResolverProps>> = (props) => {
    switch (props.tile.type) {
        case CatalogKind.Processor:
        case CatalogKind.Component:
            // eslint-disable-next-line no-case-declarations
            const iconType = props.tile.type === CatalogKind.Processor ? NodeIconType.EIP : NodeIconType.Component;
            return (
                <img
                    className={props.className}
                    src={NodeIconResolver.getIcon(props.tile.name, iconType)}
                    alt={`${props.tile.type} icon`}
                />
            );
    }
    return <img className={props.className} src={NodeIconResolver.getDefaultCamelIcon()} alt="universal adapter icon" />;
};
