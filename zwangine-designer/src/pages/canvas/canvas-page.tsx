import {FunctionComponent, JSX, ReactNode, useContext, useEffect} from 'react';
import './canvas-page.scss';
import {parse, stringify} from 'yaml';
import {
    ActionConfirmationModalContextProvider,
    CatalogContext,
    CatalogModalProvider,
    EntitiesContext,
    SourceCodeApiContext,
    SourceCodeContext,
} from "@/providers";
import {ContextToolbar, Visualization} from "@/components/visualization";
import {GrDeploy} from "react-icons/gr";
import {TbLoader} from "react-icons/tb";
import {getValue, toArray} from "@/lib/utility";
import {applyRuntimeSyntax, applySyntaxToUri} from "@/lib/ua-utils";
import {StompSessionContext} from "@/providers/stomp-session-provider.tsx";

export const CanvasPage: FunctionComponent<{ fallback?: ReactNode; additionalToolbarControls?: JSX.Element[] }> = (
    props,
) => {
    const entitiesContext = useContext(EntitiesContext);
    const catalogService = useContext(CatalogContext);
    const sourceCodeContext = useContext(SourceCodeContext);
    const stompSessionContext = useContext(StompSessionContext)
    const sourceCodeApiContext = useContext(SourceCodeApiContext);
    const visualEntities = entitiesContext?.visualEntities ?? [];


    useEffect(() => {
        stompSessionContext?.subscribeTo(`/topic/workflow.deployment`, (message => {
            console.log("workflow deployment message: ", message);
        }));
    }, []);


    async function deploySourceCode() {
        if (sourceCodeContext == "") return;
        const parsedSourceCode = parse(sourceCodeContext)[0];
        const fromCode = getValue(parsedSourceCode, "workflow.from");
        const stepsCode = getValue(parsedSourceCode, "workflow.from.steps");
        const originalUri  = getValue(parsedSourceCode, "workflow.from.uri");
        applySyntaxToUri(catalogService, originalUri, fromCode);
        applyRuntimeSyntax(catalogService, stepsCode);
        const deployableSourceCode = stringify(toArray(parsedSourceCode));
        console.log("source code to deploy: ", deployableSourceCode);
        sourceCodeApiContext.deploySourceCode(deployableSourceCode)
    }



    return (
        <CatalogModalProvider>
            <ActionConfirmationModalContextProvider>
                <div className={"grid grid-rows-[40px_auto]"}>
                    <div className="flex flex-row  items-center justify-between w-full bg-primary px-1">
                        <h4 className="text-white font-bold font-sans">Pipeline actions</h4>
                        <ContextToolbar additionalControls={props.additionalToolbarControls} />
                        <div className={`w-[40px] h-[40px] flex items-center justify-center bg-white hover:bg-secondary rounded-full ${sourceCodeContext == "" ? "cursor-not-allowed" :"cursor-pointer"}`} onClick={deploySourceCode} >
                            {!sourceCodeApiContext.deploymentLoader && <GrDeploy width="40" className={`w-[40px] text-primary font-bold`}/>}
                            {sourceCodeApiContext.deploymentLoader && <TbLoader width="40" className="w-[40px] text-primary font-bold animate-spin"/>}
                        </div>
                    </div>
                    <Visualization
                        className="canvas-page"
                        entities={visualEntities}
                        fallback={props.fallback}
                    />
                </div>
            </ActionConfirmationModalContextProvider>
        </CatalogModalProvider>
    );
};
