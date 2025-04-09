import { FunctionComponent, useCallback, useContext, useState } from 'react';
import { Catalog, ITile } from '@/components/catalog';
import { PropertiesModal } from '@/components/propertyModal';
import {CatalogTilesContext} from "@/providers";

export const CatalogPage: FunctionComponent = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalTile, setModalTile] = useState<ITile>();
    const tiles = useContext(CatalogTilesContext);

    const onTileClick = useCallback((tile: ITile) => {
        setModalTile(tile);
        setIsModalOpen(true);
    }, []);

    const handleOnClose = useCallback(() => {
        setIsModalOpen(false);
        setModalTile(undefined);
    }, []);

    return (
        <div>
            <Catalog tiles={tiles} onTileClick={onTileClick} />
            {modalTile && (
                <PropertiesModal tile={modalTile} isModalOpen={isModalOpen} onClose={handleOnClose}></PropertiesModal>
            )}
        </div>
    );
};
