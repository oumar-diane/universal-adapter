import { FunctionComponent, PropsWithChildren, RefObject, createContext, useMemo } from 'react';
import { JSONSchemaBridge as SchemaBridge } from 'uniforms-bridge-json-schema';
import {SchemaDefinition} from "@/core";
import {SchemaService} from "@/components/form";

interface SchemaBridgeProviderProps {
    schema: SchemaDefinition['schema'] | undefined;
    parentRef?: RefObject<HTMLElement>;
}

// eslint-disable-next-line react-refresh/only-export-components
export const SchemaBridgeContext = createContext<{
    schemaBridge: SchemaBridge | undefined;
    parentRef: RefObject<HTMLElement> | null;
}>({ schemaBridge: undefined, parentRef: null });

export const SchemaBridgeProvider: FunctionComponent<PropsWithChildren<SchemaBridgeProviderProps>> = (props) => {
    const value = useMemo(() => {
        const schemaService = new SchemaService();
        const schemaBridge = schemaService.getSchemaBridge(props.schema);

        return { schemaBridge, parentRef: props.parentRef ?? null };
    }, [props.parentRef, props.schema]);

    return <SchemaBridgeContext.Provider value={value}>{props.children}</SchemaBridgeContext.Provider>;
};
