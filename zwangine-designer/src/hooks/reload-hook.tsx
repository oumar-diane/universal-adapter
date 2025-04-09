import { ReloadContext } from '@/providers';
import { FunctionComponent, PropsWithChildren, useCallback, useMemo, useState } from 'react';

/** The goal for this hook is to provide a way to reload the page when needed. */
export const useReload = () => {
    const [lastRender, setLastRender] = useState(Date.now());

    const reloadPage = useCallback(() => {
        setLastRender(Date.now());
    }, []);

    const value = useMemo(() => ({ lastRender, reloadPage }), [lastRender, reloadPage]);

    const Provider: FunctionComponent<PropsWithChildren> = (props) => (
        <ReloadContext.Provider value={value}>{props.children}</ReloadContext.Provider>
    );

    return Provider;
};
