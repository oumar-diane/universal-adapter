import {
    FunctionComponent,
    PropsWithChildren,
    createContext,
    useCallback,
    useLayoutEffect,
    useMemo,
    useState,
} from 'react';
import {EventNotifier} from "@/lib/utility";
import {useStompSession} from "@/hooks/stomp-session-hook.tsx";

interface ISourceCodeApi {
    deploymentLoader: boolean;
    /** Deploy Source Code */
    handleDeploymentLoader: (status: boolean) => void;
    /** Deploy Source Code */
    deploySourceCode: (deployableSourceCode:string) => void;
    /** Set the Source Code and notify subscribers */
    setCodeAndNotify: (sourceCode: string, path?: string) => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const SourceCodeContext = createContext<string>('');
// eslint-disable-next-line react-refresh/only-export-components
export const SourceCodeApiContext = createContext<ISourceCodeApi>({ setCodeAndNotify: () => {}, deploySourceCode: () => {}, handleDeploymentLoader: () => {}, deploymentLoader: false });

export const SourceCodeProvider: FunctionComponent<PropsWithChildren> = (props) => {
    const eventNotifier = EventNotifier.getInstance();
    const [sourceCode, setSourceCode] = useState<string>('');
    const stompSession = useStompSession()
    const [deploymentLoader, setDeploymentLoader] = useState<boolean>(false);

    useLayoutEffect(() => {
        return eventNotifier.subscribe('entities:updated', (code) => {
            setSourceCode(code);
        });
    }, [eventNotifier]);

    const setCodeAndNotify = useCallback(
        (code: string, path?: string) => {
            setSourceCode(code);
            eventNotifier.next('code:updated', { code, path });
        },
        [eventNotifier],
    );

    const deploySourceCode = useCallback(
        (deployableSourceCode:string) => {
            if(sourceCode != '') {
                //setDeploymentLoader(true)
                stompSession.sendMessage("/app/workflow.deploy", deployableSourceCode);
            }
        }, [stompSession, sourceCode]
    )

    const handleDeploymentLoader = useCallback(
        (status: boolean) => {
            setDeploymentLoader(status);
        },
        [],
    );

    const sourceCodeApi: ISourceCodeApi = useMemo(
        () => ({
            deploymentLoader,
            handleDeploymentLoader,
            setCodeAndNotify,
            deploySourceCode
        }),
        [deploySourceCode, deploymentLoader, handleDeploymentLoader, setCodeAndNotify],
    );

    return (
        <SourceCodeApiContext.Provider value={sourceCodeApi}>
            <SourceCodeContext.Provider value={sourceCode}>{props.children}</SourceCodeContext.Provider>
        </SourceCodeApiContext.Provider>
    );
};
