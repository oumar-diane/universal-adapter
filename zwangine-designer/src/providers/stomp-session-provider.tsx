import {createContext, FunctionComponent, PropsWithChildren, useEffect, useRef, useState} from "react";
import {ActivationState, Client} from "@stomp/stompjs";

interface StompSessionProviderType {
    /** The URL of the STOMP server */
    url: string;
    /** The topic to subscribe to */
    subscribeTo: (topic: string, callback:(message:string)=>void) => void;
    /** The callback function to handle incoming messages */
    sendMessage: (topic: string, message: any) => void;
    
}

// eslint-disable-next-line react-refresh/only-export-components
export const StompSessionContext = createContext<StompSessionProviderType|undefined>(undefined);

export const StompSessionProvider :FunctionComponent<PropsWithChildren<{url?:StompSessionProviderType["url"]}>> = (props) => {
    const [topicToSubscribe, setTopicToSubscribe] = useState<{topic:string, callback:(message:any)=>void}[]>([]);
    const url  = props.url || 'ws://localhost:8080/ws';
    const stompClientRef = useRef<Client>(undefined);

    useEffect(() => {
        stompClientRef.current = new Client({
            brokerURL: url,
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        stompClientRef.current?.activate();

        return () => {
            stompClientRef.current?.deactivate();
        };
    }, [topicToSubscribe, url]);

    const subscribeTo = (topic: string, callback:(message:any)=>void) => {
        setTopicToSubscribe([...topicToSubscribe, {topic, callback}]);
        if(stompClientRef.current?.state === ActivationState.ACTIVE ){
            topicToSubscribe.forEach(({topic, callback}) => {
                stompClientRef.current?.subscribe(topic, (message) => {
                    callback(message);
                });
            });
        }
    };
    
    const sendMessage = (topic: string, message: string) => {
        stompClientRef.current?.publish({ destination: topic, body: message });
    };
    
    const stompSessionApi: StompSessionProviderType = {
        url,
        subscribeTo,
        sendMessage,
    }
    
    return (
        <StompSessionContext.Provider value={stompSessionApi}>
            {props.children}
        </StompSessionContext.Provider>
    );
}
