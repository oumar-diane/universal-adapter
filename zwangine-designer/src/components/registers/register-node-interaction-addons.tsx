import { FunctionComponent, PropsWithChildren, useContext, useRef } from 'react';
import {IInteractionAddonType, IRegisteredInteractionAddon, NodeInteractionAddonContext} from '.';

export const RegisterNodeInteractionAddons: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const { registerInteractionAddon } = useContext(NodeInteractionAddonContext);
    const addonsToRegister = useRef<IRegisteredInteractionAddon[]>([
        {
            type: IInteractionAddonType.ON_DELETE,
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            activationFn: (_)=>true,
            callback: () => {
            },
            modalCustomization: {
                additionalText: 'Do you also want to delete the associated Kaoto DataMapper mapping file (XSLT)?',
                buttonOptions: {},
            },
        },
    ]);

    addonsToRegister.current.forEach((interaction) => {
        registerInteractionAddon(interaction);
    });

    return <>{children}</>;
};
