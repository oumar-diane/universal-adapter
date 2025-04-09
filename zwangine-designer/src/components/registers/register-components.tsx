import { FunctionComponent, PropsWithChildren, useContext, useRef } from 'react';
import {IRegisteredComponent, RenderingAnchorContext} from '@/components/renderingAnchor';

export const RegisterComponents: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const { registerComponent } = useContext(RenderingAnchorContext);

    const componentsToRegister = useRef<IRegisteredComponent[]>([]);

    componentsToRegister.current.forEach((regComponent) => registerComponent(regComponent));

    return <>{children}</>;
};
