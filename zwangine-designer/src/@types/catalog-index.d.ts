/* tslint:disable */
/* eslint-disable */


export interface CatalogDefinition {
    name: string;
    catalogs: { [index: string]: CatalogDefinitionEntry };
    schemas: { [index: string]: CatalogDefinitionEntry };
}

export interface CatalogDefinitionEntry {
    name: string;
    description: string;
    file: string;
}

export interface CatalogLibrary {
    definitions: CatalogLibraryEntry[];
    version: number;
    name: string;
}

export interface CatalogLibraryEntry {
    name: string;
    version: string;
    runtime: string;
    fileName: string;
}
