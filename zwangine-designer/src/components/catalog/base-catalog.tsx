import {CatalogLayout, ITile} from "@/components/catalog/catalog-models.ts";
import {DataList, Gallery, Pagination} from "@patternfly/react-core";
import "./base-catalog.scss"
import {FunctionComponent, useCallback, useEffect, useRef, useState} from "react"
import {Tile} from "@/components/catalog/tile.tsx";
import {CatalogDataListItem} from "@/components/catalog/data-list-item.tsx";


interface BaseCatalogProps{
    className?: string;
    tiles:ITile[];
    catalogLayout:CatalogLayout;
    onTileClick:(title:ITile) => void;
    onTagClick:(_event:unknown, value:string) => void;
}

export const BaseCatalog: FunctionComponent<BaseCatalogProps> = (props) => {
    const catalogBodyRef = useRef<HTMLDivElement>(null)
    const itemCount = props.tiles.length
    const [page, setPage] = useState(1)
    const [perPage, setPerPage] = useState(50)

    const onTileClick = useCallback((title:ITile)=>{
        props.onTileClick?.(title)
    }, [props])

    const startIndex = (page-1)*perPage<0 ? 0:(page-1)*perPage;
    const endIndex = page*perPage;

    useEffect(()=>{
        // handling scenario where itemCount is less than the page selected.
        if(startIndex + 1 > itemCount){
            setPage(Math.ceil(itemCount/perPage))
        }else if(page===0 && itemCount>0){
            setPage(1)
        }
    }, [props.tiles])

    const onSelectDataListItem = useCallback(
        (_event:React.MouseEvent | React.KeyboardEvent | MouseEvent, id:string)=>{
            const tile = props.tiles.find((tile)=>tile.name+'-'+tile.type === id)
            onTileClick(tile!)
        },
        [onTileClick, props]
    )

    const onSetPage = (_event:React.MouseEvent | React.KeyboardEvent | MouseEvent, newPage:number)=>{
        setPage(newPage)
    }

    const onPerPageSelect = (_event:React.MouseEvent | React.KeyboardEvent | MouseEvent, perPage:number, newPage:number)=>{
        setPage(newPage)
        setPerPage(perPage)
    }

    const paginatedCards = props.tiles.slice(startIndex, endIndex)

    return (
        <>
            <Pagination
                itemCount={itemCount}
                perPage={perPage}
                page={page}
                onSetPage={onSetPage}
                widgetId="catalog-pagination"
                onPerPageSelect={onPerPageSelect}
                ouiaId="CatalogPagination"
            />
            <div id="catalog-list" className="catalog-list" ref={catalogBodyRef}>
                {props.catalogLayout == CatalogLayout.List && (
                    <DataList aria-label="Catalog list" onSelectDataListItem={onSelectDataListItem} isCompact>
                        {paginatedCards.map((tile) => (
                            <CatalogDataListItem key={`${tile.name}-${tile.type}`} tile={tile} onTagClick={props.onTagClick} />
                        ))}
                    </DataList>
                )}
                {props.catalogLayout == CatalogLayout.Gallery && (
                    <Gallery hasGutter>
                        {paginatedCards.map((tile) => (
                            <Tile key={`${tile.name}-${tile.type}`} tile={tile} onClick={onTileClick} onTagClick={props.onTagClick} />
                        ))}
                    </Gallery>
                )}
            </div>
        </>
    )
}